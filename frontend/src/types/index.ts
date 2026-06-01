// ─── Entidades do domínio (espelham os DTOs do backend) ──────────────────────

export interface UsuarioResponse {
  id: string;
  nome: string;
  email: string;
  dataCadastro: string;
}

export interface LoginResponse {
  id: string;
  nome: string;
  email: string;
  token: string;
  mensagem: string;
}

export interface CategoriaResponse {
  id: string;
  usuarioId: string | null;
  nome: string;
  icone: string;
}

export interface TransacaoResponse {
  id: string;
  usuarioId: string;
  categoriaId: string;
  categoriaNome: string;
  valor: number;
  tipo: "receita" | "despesa";
  data: string;
  descricao: string;
}

export interface GastoPorCategoria {
  categoriaId: string;
  categoriaNome: string;
  categoriaIcone: string;
  totalGasto: number;
  quantidadeTransacoes: number;
}

export interface DashboardResponse {
  saldoTotal: number;
  receitasMes: number;
  despesasMes: number;
  quantidadeTransacoesMes: number;
  ultimas5Transacoes: TransacaoResponse[];
  gastosPorCategoria: GastoPorCategoria[];
}

// ─── Request DTOs ────────────────────────────────────────────────────────────

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface RegistroRequest {
  nome: string;
  email: string;
  senha: string;
}

export interface TransacaoRequest {
  categoriaId: string;
  valor: number;
  tipo: string;
  data: string;
  descricao: string;
}

export interface CategoriaRequest {
  nome: string;
  icone: string;
}

// ─── Filtros ─────────────────────────────────────────────────────────────────

export interface TransacaoFiltro {
  mes?: number;
  ano?: number;
  categoriaId?: string;
  tipo?: string;
}

// ─── Auth Context ────────────────────────────────────────────────────────────

export interface AuthUser {
  id: string;
  nome: string;
  email: string;
  token: string;
}

export interface AuthContextData {
  user: AuthUser | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  login: (email: string, senha: string) => Promise<void>;
  register: (nome: string, email: string, senha: string) => Promise<void>;
  logout: () => Promise<void>;
}

// ─── Erros da API ────────────────────────────────────────────────────────────

export interface ApiError {
  status: number;
  mensagem: string;
  timestamp: string;
}
