import api from "./api";
import type { CategoriaRequest, CategoriaResponse } from "../types";

export const categoriaService = {
  async listarMinhas(): Promise<CategoriaResponse[]> {
    const response = await api.get<CategoriaResponse[]>("/categorias");
    return response.data;
  },

  async listarGlobais(): Promise<CategoriaResponse[]> {
    const response = await api.get<CategoriaResponse[]>("/categorias/globais");
    return response.data;
  },

  async buscarPorId(id: string): Promise<CategoriaResponse> {
    const response = await api.get<CategoriaResponse>(`/categorias/${id}`);
    return response.data;
  },

  async criar(data: CategoriaRequest): Promise<CategoriaResponse> {
    const response = await api.post<CategoriaResponse>("/categorias", data);
    return response.data;
  },

  async atualizar(id: string, data: CategoriaRequest): Promise<CategoriaResponse> {
    const response = await api.put<CategoriaResponse>(`/categorias/${id}`, data);
    return response.data;
  },

  async deletar(id: string): Promise<void> {
    await api.delete(`/categorias/${id}`);
  },
};
