/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}"
  ],
  theme: {
    extend: {
      screens: {
        'mobile': '640px',
        'tablet': '768px',
        'laptop': '1024px',
        'desktop': '1280px',
        'lScreen': '1536px',
        'xlScreen': '1824px',
        '4k':'2560px'
      },
    },
    container: {
      padding: '2rem',
    }
  },
  plugins: [
    require('tailwindcss-primeui')
  ],
}

