import { defineUnlighthouseConfig } from 'unlighthouse/config'

export default defineUnlighthouseConfig({
  site: 'http://michibaum.ch/',
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
