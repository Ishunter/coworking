package control;

import control.exceptions.ExceptionCancelar;
import control.exceptions.ExceptionInput;
import control.exceptions.ExceptionSalvoComSucesso;
import java.util.ArrayList;
import model.Cliente;
import model.ClienteDAO;
import view.FrameMenu;

public class ControladorCliente {

    private static ControladorCliente instance = null;
    private ArrayList<Cliente> Adimplente = new ArrayList<>();
    private ArrayList<Cliente> Inadimplente = new ArrayList<>();
    private FrameMenu view;

    private ControladorCliente() {
    }

    public static ControladorCliente getInstance() {
        if (instance == null) {
            instance = new ControladorCliente();
        }
        return instance;
    }

    public void salvar(String Atividade, String cpf, String endereco, boolean estaInadimplente, String idade, String nome, String observacao) throws ExceptionInput, ExceptionSalvoComSucesso, ExceptionCancelar {
        // not null check
        if (cpf.isEmpty() || nome.isEmpty() || idade.isEmpty()) {
            throw new ExceptionInput("Por favor preencher campos.");
        }
        // PK Check 
        if (isCpfInUse(cpf)) {
            boolean atualizarDados = CtrlUtil.getInstance().PanelYesOrNo(
                "Já existe um Cliente com este CPF.\nDeseja atualizar os dados?",
                "Deseja atualizar os dados?");
            if (atualizarDados) {
                ClienteDAO.getInstance().update(Atividade, cpf, endereco, estaInadimplente, Integer.valueOf(idade), nome, observacao);
                throw new ExceptionSalvoComSucesso("Cliente");
            } else {
                throw new ExceptionCancelar();
            }
        } else {
            ClienteDAO.getInstance().create(Atividade, cpf, endereco, Integer.valueOf(idade), nome, observacao);
            throw new ExceptionSalvoComSucesso("Cliente");
        }
    }

    public ArrayList<String> getAllClientesLocacao() {
        ArrayList<String> array = new ArrayList<>();
        for (Cliente c : ClienteDAO.getInstance().getAll()) {
            if (!c.isEstaInadimplente()) {
                array.add(c.getCpf() + "#" + c.getNome());
            }
        }
        return array;
    }

    public Cliente getCli(String cpf) {
        return ClienteDAO.getInstance().read(cpf);
    }

    public ArrayList<String> getAllClientes() {
        ArrayList<String> array = new ArrayList<>();
        for (Cliente c : ClienteDAO.getInstance().getAll()) {
            array.add(c.getCpf() + "#" + c.getNome());
        }
        return array;
    }

    public ArrayList<String> getClienteView(String cpf) {
        ArrayList<String> array = new ArrayList<>();
        Cliente c = ClienteDAO.getInstance().read(cpf);
        if (c != null) {
            array.add(c.getAtividade());
            array.add(c.getCpf());
            array.add(c.getEndereco());
            array.add(String.valueOf(c.isEstaInadimplente()));
            array.add(String.valueOf(c.getIdade()));
            array.add(c.getNome());
            array.add(c.getObservacao());
        }
        return array;
    }

    public void setView(FrameMenu view) {
        this.view = view;
        this.view.setVisible(false);
    }

    public void exit() {
        this.view.setVisible(true);
    }

    public boolean isCpfInUse(String cpf) {
        return ClienteDAO.getInstance().read(cpf) != null;
    }

    public void loadInadimplente() {
        ArrayList<Cliente> clientes = ClienteDAO.getInstance().getAll();
        Adimplente = new ArrayList<>();
        Inadimplente = new ArrayList<>();
        for (Cliente c : clientes) {
            if (c.isEstaInadimplente()) {
                Inadimplente.add(c);
            } else {
                Adimplente.add(c);
            }
        }
    }

    public void addAdimplente(String cpf) {
        Cliente c = ClienteDAO.getInstance().read(cpf.substring(0, cpf.indexOf("#")));
        if (c != null) {
            for (Cliente cli : Inadimplente) {
                if (cli.getCpf().equals(c.getCpf())) {
                    Inadimplente.remove(cli);
                    break;
                }
            }
            this.Adimplente.add(c);
        }
    }

    public ArrayList<String> getAdimplente() {
        ArrayList<String> array = new ArrayList<>();
        if (this.Adimplente != null) {
            for (Cliente cli : this.Adimplente) {
                array.add(cli.getCpf() + "#" + cli.getNome());
            }
        }
        return array;
    }

    public void addInadimplente(String cpf) {
        Cliente c = ClienteDAO.getInstance().read(cpf.substring(0, cpf.indexOf("#")));
        if (c != null) {
            this.Inadimplente.add(c);
            for (Cliente cli : Adimplente) {
                if (cli.getCpf().equals(c.getCpf())) {
                    Adimplente.remove(cli);
                    break;
                }
            }
        }
    }

    public ArrayList<String> getInadimplente() {
        ArrayList<String> array = new ArrayList<>();
        if (this.Inadimplente != null) {
            for (Cliente cli : this.Inadimplente) {
                array.add(cli.getCpf() + "#" + cli.getNome());
            }
        }
        return array;
    }

    public void salvarInadimplencia() throws ExceptionSalvoComSucesso {
        ArrayList<Cliente> clientes = ClienteDAO.getInstance().getAll();
        for (Cliente c : clientes) {
            for (Cliente a : Adimplente) {
                if (a.getCpf().equals(c.getCpf())) {
                    if (c.isEstaInadimplente()) {
                        c.setEstaInadimplente(false);
                        ClienteDAO.getInstance().update(c);
                        break;
                    }
                }
            }
            for (Cliente i : Inadimplente) {
                if (i.getCpf().equals(c.getCpf())) {
                    if (!c.isEstaInadimplente()) {
                        c.setEstaInadimplente(true);
                        ClienteDAO.getInstance().update(c);
                        break;
                    }
                }
            }
        }
        loadInadimplente();
        throw new ExceptionSalvoComSucesso("Inadimplencia");
    }
}
