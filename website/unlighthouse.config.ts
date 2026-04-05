import { defineUnlighthouseConfig } from 'unlighthouse/config'

const isProd = process.env["NODE_ENV"] === 'production'

export default defineUnlighthouseConfig({
  site: isProd ? 'https://michibaum.ch/' : 'http://michibaum.ch/',
  scanner: {
    skipJavascript: false,
    samples: 3,
    throttle: true,
    sitemap: [
      '/sitemap.xml',
    ],
  },
  lighthouseOptions: {
    maxWaitForLoad: 10000, // Wait up to 10s for page load
  },
})
