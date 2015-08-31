package br.com.mobilesaude.multiselectasgmail;

/**
 * Created by victor on 27/08/15.
 */
public class ItemTO {

    private String descricao;
    private boolean checked;

    public ItemTO(String descricao) {
        this.descricao = descricao;
        this.checked = false;
    }

    public ItemTO() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
