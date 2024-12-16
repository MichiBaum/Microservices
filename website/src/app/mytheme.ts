import Lara from "@primeng/themes/lara";
import { definePreset } from "@primeng/themes";

export const MyPreset = definePreset(Lara, {
  semantic: {
    primary: {
      50: "#fffbf2",
      100: "#ffe9c2",
      200: "#ffd891",
      300: "#ffc761",
      400: "#ffb630",
      500: "#ffa500",
      600: "#d98c00",
      700: "#b37300",
      800: "#8c5b00",
      900: "#664200",
      950: "#402900"
    },
    colorScheme: {
      light: {
        surface: {
          0: "#ffffff",
          50: "#f8fafc",
          100: "#f1f5f9",
          200: "#e2e8f0",
          300: "#cbd5e1",
          400: "#94a3b8",
          500: "#64748b",
          600: "#475569",
          700: "#334155",
          800: "#1e293b",
          900: "#0f172a",
          950: "#020617"
        },
        primary: {
          color: "{primary.500}"
        }
      },
      dark: {
        surface: {
          0: "#ffffff",
          50: "#fafafa",
          100: "#f4f4f5",
          200: "#e4e4e7",
          300: "#d4d4d8",
          400: "#a1a1aa",
          500: "#71717a",
          600: "#52525b",
          700: "#3f3f46",
          800: "#27272a",
          900: "#18181b",
          950: "#09090b"
        },
        primary: {
          color: "{primary.500}",
        }
      }
    }
  }
})
