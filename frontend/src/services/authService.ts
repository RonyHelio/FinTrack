import api from "./api";
import type {
  LoginRequest,
  LoginResponse,
  RegistroRequest,
  UsuarioResponse,
} from "../types";

export const authService = {
  async login(data: LoginRequest): Promise<LoginResponse> {
    const response = await api.post<LoginResponse>("/auth/login", data);
    return response.data;
  },

  async register(data: RegistroRequest): Promise<UsuarioResponse> {
    const response = await api.post<UsuarioResponse>("/auth/register", data);
    return response.data;
  },
};
