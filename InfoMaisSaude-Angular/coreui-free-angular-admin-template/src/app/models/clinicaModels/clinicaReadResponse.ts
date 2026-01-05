export interface ClinicaReadResponse {
    id: number;
    nome: string;
    cnpj: string;
    endereco: string;
    telefone: string;
    site: string;
    email: string;
    horarioFuncionamentoInicio: string;
    horarioFuncionamentoFinal: string;
    latitude: number;  
    longitude: number; 
    especializacoes: string[];
}