export const API_BASE_URL = "http://localhost:8080/api"; // URL do backend rodando no host

export const COLORS = {
  primary: {
    50:  "#EEF2FF",
    100: "#E0E7FF",
    200: "#C7D2FE",
    300: "#A5B4FC",
    400: "#818CF8",
    500: "#6366F1",
    600: "#4F46E5",
    700: "#4338CA",
    800: "#3730A3",
    900: "#312E81",
    950: "#1E1B4B",
  },
  success: {
    50:  "#ECFDF5",
    500: "#10B981",
    600: "#059669",
  },
  danger: {
    50:  "#FEF2F2",
    500: "#EF4444",
    600: "#DC2626",
  },
  warning: {
    50:  "#FFFBEB",
    500: "#F59E0B",
    600: "#D97706",
  },
  dark: {
    bg:      "#0F172A",
    card:    "#1E293B",
    surface: "#334155",
    border:  "#475569",
    text:    "#F8FAFC",
    muted:   "#94A3B8",
  },
} as const;

export const STORAGE_KEYS = {
  AUTH_TOKEN: "fintrack_auth_token",
  USER_DATA: "fintrack_user_data",
} as const;

export const CATEGORY_ICONS: Record<string, string> = {
  "Alimentação": "🍔",
  "Transporte":  "🚗",
  "Moradia":     "🏠",
  "Lazer":       "🎮",
  "Saúde":       "💊",
  "Educação":    "📚",
  "Salário":     "💰",
  "Investimentos": "📈",
} as const;
