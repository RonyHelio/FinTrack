import api from "./api";
import type {
  DashboardResponse,
  TransacaoFiltro,
  TransacaoRequest,
  TransacaoResponse,
} from "../types";

export const transacaoService = {
  async listar(): Promise<TransacaoResponse[]> {
    const response = await api.get<TransacaoResponse[]>("/transacoes");
    return response.data;
  },

  async buscarPorId(id: string): Promise<TransacaoResponse> {
    const response = await api.get<TransacaoResponse>(`/transacoes/${id}`);
    return response.data;
  },

  async criar(data: TransacaoRequest): Promise<TransacaoResponse> {
    const response = await api.post<TransacaoResponse>("/transacoes", data);
    return response.data;
  },

  async atualizar(id: string, data: TransacaoRequest): Promise<TransacaoResponse> {
    const response = await api.put<TransacaoResponse>(`/transacoes/${id}`, data);
    return response.data;
  },

  async deletar(id: string): Promise<void> {
    await api.delete(`/transacoes/${id}`);
  },

  async filtrar(filtros: TransacaoFiltro): Promise<TransacaoResponse[]> {
    const params = new URLSearchParams();
    if (filtros.mes !== undefined) params.append("mes", filtros.mes.toString());
    if (filtros.ano !== undefined) params.append("ano", filtros.ano.toString());
    if (filtros.categoriaId) params.append("categoriaId", filtros.categoriaId);
    if (filtros.tipo) params.append("tipo", filtros.tipo);

    const response = await api.get<TransacaoResponse[]>(`/transacoes/filtro?${params.toString()}`);
    return response.data;
  },

  async getDashboard(): Promise<DashboardResponse> {
    const response = await api.get<DashboardResponse>("/dashboard");
    return response.data;
  },
};
