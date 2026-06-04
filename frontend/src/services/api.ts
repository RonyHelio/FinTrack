import axios, { AxiosError, InternalAxiosRequestConfig } from "axios";
import * as SecureStore from "../utils/storage";
import { API_BASE_URL, STORAGE_KEYS } from "../constants";
import type { ApiError } from "../types";

/**
 * Instância Axios configurada para a API do FinTrack.
 *
 * Interceptors:
 * - Request: injeta o JWT do SecureStore no header Authorization
 * - Response: extrai mensagens de erro padronizadas do backend
 */
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  headers: {
    "Content-Type": "application/json",
  },
});

// ─── Request Interceptor: injeta JWT ─────────────────────────────────────────

api.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    const token = await SecureStore.getItemAsync(STORAGE_KEYS.AUTH_TOKEN);
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// ─── Response Interceptor: trata erros padronizados ──────────────────────────

api.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiError>) => {
    // ─── Auto-logout: token inválido ou expirado ─────────────────
    if (error.response?.status === 401 || error.response?.status === 403) {
      // Limpa token velho do navegador e força volta à tela de login
      SecureStore.deleteItemAsync("fintrack_auth_token");
      SecureStore.deleteItemAsync("fintrack_user_data");
      if (typeof window !== "undefined") {
        window.location.reload();
      }
    }

    if (error.response?.data?.mensagem) {
      const apiError: ApiError = {
        status: error.response.status,
        mensagem: error.response.data.mensagem,
        timestamp: error.response.data.timestamp || new Date().toISOString(),
      };
      return Promise.reject(apiError);
    }

    if (error.code === "ECONNABORTED") {
      return Promise.reject({
        status: 408,
        mensagem: "A conexão com o servidor expirou. Tente novamente.",
        timestamp: new Date().toISOString(),
      } as ApiError);
    }

    if (!error.response) {
      return Promise.reject({
        status: 0,
        mensagem: "Sem conexão com o servidor. Verifique sua internet.",
        timestamp: new Date().toISOString(),
      } as ApiError);
    }

    return Promise.reject({
      status: error.response.status,
      mensagem: "Erro inesperado. Tente novamente mais tarde.",
      timestamp: new Date().toISOString(),
    } as ApiError);
  }
);

export default api;
