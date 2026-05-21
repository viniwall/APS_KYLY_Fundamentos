import type { Config } from 'tailwindcss'

export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#1B3A57',
          dark: '#0F2638',
        },
        accent: '#E0A800',
        surface: '#F5F5F5',
        divider: '#E0E0E0',
        text: {
          primary: '#111111',
          secondary: '#666666',
        },
        success: '#2E7D32',
        error: '#C62828',
        warning: '#F9A825',
        info: '#1976D2',
      },
      fontFamily: {
        sans: ['system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
} satisfies Config
