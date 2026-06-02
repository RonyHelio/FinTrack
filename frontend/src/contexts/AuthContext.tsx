import React, { createContext, useContext, useEffect, useState, useCallback } from "react";
import * as SecureStore from "../utils/storage";
import { STORAGE_KEYS } from "../constants";
import { authService } from "../services/authService";
import type { AuthContextData, AuthUser, ApiError } from "../types";

const AuthContext = createContext<AuthContextData>({} as AuthContextData);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // ─── Restaura sessão do SecureStore ao abrir o app ──────────────────────────

  useEffect(() => {
    async function loadStoredUser() {
      try {
        const token = await SecureStore.getItemAsync(STORAGE_KEYS.AUTH_TOKEN);
        const userData = await SecureStore.getItemAsync(STORAGE_KEYS.USER_DATA);

        if (token && userData) {
          const parsed: AuthUser = JSON.parse(userData);
          setUser({ ...parsed, token });
        }
      } catch (error) {
        console.error("Erro ao restaurar sessão:", error);
        await clearStorage();
      } finally {
        setIsLoading(false);
      }
    }

    loadStoredUser();
  }, []);

  // ─── Login ──────────────────────────────────────────────────────────────────

  const login = useCallback(async (email: string, senha: string) => {
    const response = await authService.login({ email, senha });

    const authUser: AuthUser = {
      id: response.id,
      nome: response.nome,
      email: response.email,
      token: response.token,
    };

    await SecureStore.setItemAsync(STORAGE_KEYS.AUTH_TOKEN, response.token);
    await SecureStore.setItemAsync(STORAGE_KEYS.USER_DATA, JSON.stringify(authUser));

    setUser(authUser);
  }, []);

  // ─── Registro ───────────────────────────────────────────────────────────────

  const register = useCallback(async (nome: string, email: string, senha: string) => {
    await authService.register({ nome, email, senha });
    // Após registrar, faz login automaticamente para obter o token
    await login(email, senha);
  }, [login]);

  // ─── Logout ─────────────────────────────────────────────────────────────────

  const logout = useCallback(async () => {
    await clearStorage();
    setUser(null);
  }, []);

  // ─── Helper ─────────────────────────────────────────────────────────────────

  async function clearStorage() {
    await SecureStore.deleteItemAsync(STORAGE_KEYS.AUTH_TOKEN);
    await SecureStore.deleteItemAsync(STORAGE_KEYS.USER_DATA);
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        isLoading,
        isAuthenticated: !!user,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

/**
 * Hook para acessar o contexto de autenticação.
 * Lança erro se usado fora do AuthProvider.
 */
export function useAuth(): AuthContextData {
  const context = useContext(AuthContext);
  if (!context || Object.keys(context).length === 0) {
    throw new Error("useAuth deve ser usado dentro de um <AuthProvider>");
  }
  return context;
}
