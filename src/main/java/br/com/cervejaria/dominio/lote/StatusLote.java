package br.com.cervejaria.dominio.lote;

import java.util.EnumSet;
import java.util.Set;

/**
 * Estados possíveis de um lote de produção.
 * 
 * <p>
 * Implementa a máquina de estados do ciclo de vida do lote.
 * </p>
 * 
 * @author Sistema Cervejaria
 * @version 1.0
 */
public enum StatusLote {

    PLANEJADO("Planejado", false) {
        @Override
        public Set<StatusLote> transicoesPermitidas() {
            return EnumSet.of(EM_BRASSAGEM, DESCARTADO);
        }
    },

    EM_BRASSAGEM("Em Brassagem", false) {
        @Override
        public Set<StatusLote> transicoesPermitidas() {
            return EnumSet.of(FERMENTANDO, DESCARTADO);
        }
    },

    FERMENTANDO("Fermentando", false) {
        @Override
        public Set<StatusLote> transicoesPermitidas() {
            return EnumSet.of(MATURANDO, DESCARTADO);
        }
    },

    MATURANDO("Maturando", false) {
        @Override
        public Set<StatusLote> transicoesPermitidas() {
            return EnumSet.of(PRONTO_PARA_ENVASE, DESCARTADO);
        }
    },

    PRONTO_PARA_ENVASE("Pronto para Envase", false) {
        @Override
        public Set<StatusLote> transicoesPermitidas() {
            return EnumSet.of(ENVASADO, MATURANDO, DESCARTADO);
        }
    },

    ENVASADO("Envasado", true) {
        @Override
        public Set<StatusLote> transicoesPermitidas() {
            return EnumSet.noneOf(StatusLote.class); // Estado final
        }
    },

    DESCARTADO("Descartado", true) {
        @Override
        public Set<StatusLote> transicoesPermitidas() {
            return EnumSet.noneOf(StatusLote.class); // Estado final
        }
    };

    private final String descricao;
    private final boolean estadoFinal;

    StatusLote(String descricao, boolean estadoFinal) {
        this.descricao = descricao;
        this.estadoFinal = estadoFinal;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Indica se este é um estado final (não permite mais transições).
     */
    public boolean isEstadoFinal() {
        return estadoFinal;
    }

    /**
     * Retorna o conjunto de estados para os quais é possível transicionar.
     */
    public abstract Set<StatusLote> transicoesPermitidas();

    /**
     * Verifica se é possível transicionar para o estado especificado.
     */
    public boolean podeTransicionarPara(StatusLote novoStatus) {
        return transicoesPermitidas().contains(novoStatus);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
