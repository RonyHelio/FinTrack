import React, { createContext, useContext, useEffect, useState } from "react";
import { useColorScheme as useNativeWindColorScheme } from "nativewind";

type Theme = "light" | "dark";

interface ThemeContextData {
  theme: Theme;
  toggleTheme: () => void;
  isDark: boolean;
}

const ThemeContext = createContext<ThemeContextData>({} as ThemeContextData);

export function ThemeProvider({ children }: { children: React.ReactNode }) {
  const { colorScheme, setColorScheme } = useNativeWindColorScheme();
  const [theme, setTheme] = useState<Theme>((colorScheme as Theme) || "dark");

  useEffect(() => {
    // Sincroniza o NativeWind caso não esteja usando
    if (colorScheme !== theme) {
      setColorScheme(theme);
    }
  }, [theme, colorScheme, setColorScheme]);

  const toggleTheme = () => {
    setTheme((prev) => (prev === "dark" ? "light" : "dark"));
  };

  return (
    <ThemeContext.Provider value={{ theme, toggleTheme, isDark: theme === "dark" }}>
      {children}
    </ThemeContext.Provider>
  );
}

export function useTheme() {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error("useTheme must be used within a ThemeProvider");
  }
  return context;
}
