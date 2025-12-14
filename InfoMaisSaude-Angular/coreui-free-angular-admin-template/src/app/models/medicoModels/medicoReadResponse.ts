import { AgendaItem } from "./agendaItem";

export interface MedicoReadResponse {
    id: number;
    nome: string;
    especializacao: string;
    telefone: string;
    agenda: AgendaItem[];
    login: string;
    senha: string;
}