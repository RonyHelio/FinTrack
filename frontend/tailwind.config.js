/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: "class",
  content: [
    "./App.{ts,tsx}",
    "./app/**/*.{ts,tsx}",
    "./src/**/*.{ts,tsx}",
  ],
  presets: [require("nativewind/preset")],
  theme: {
    extend: {
      colors: {
        // ─── Paleta principal FinTrack ──────────────────────────
        primary: {
          50:  "#EEF2FF",
          100: "#E0E7FF",
          200: "#C7D2FE",
          300: "#A5B4FC",
          400: "#818CF8",
          500: "#6366F1",  // cor principal
          600: "#4F46E5",
          700: "#4338CA",
          800: "#3730A3",
          900: "#312E81",
          950: "#1E1B4B",
        },
        // ─── Cores semânticas ─────────────────────────────────
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
        // ─── Background dark mode ─────────────────────────────
        dark: {
          bg:      "#0F172A",
          card:    "#1E293B",
          surface: "#334155",
          border:  "#475569",
          text:    "#F8FAFC",
          muted:   "#94A3B8",
        },
      },
      fontFamily: {
        sans: ["Inter_400Regular"],
        medium: ["Inter_500Medium"],
        semibold: ["Inter_600SemiBold"],
        bold: ["Inter_700Bold"],
      },
    },
  },
  plugins: [],
};
