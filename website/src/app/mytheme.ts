import Lara from "@primeng/themes/lara";
import { definePreset } from "@primeng/themes";

export const MyPreset = definePreset(Lara, {
  semantic: {
    primary: {
      50: "#fffbeb",
      100: "#fef3c7",
      200: "#fde68a",
      300: "#fcd34d",
      400: "#fbbf24",
      500: "#f6a010",
      600: "#d97706",
      700: "#b45309",
      800: "#92400e",
      900: "#78350f",
      950: "#451a03"
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
          50: "#fbfbfb",
          100: "#ebebeb",
          200: "#dbdbdb",
          300: "#cbcbcb",
          400: "#bbbbbb",
          500: "#ababab",
          600: "#919191",
          700: "#787878",
          800: "#5e5e5e",
          900: "#444444",
          950: "#2b2b2b"
        },
        primary: {
          color: "{primary.500}",
        }
      }
    }
  }
})
