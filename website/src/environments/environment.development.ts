export const environment = {
  base_url: (protocol: string, domain: string) => `${protocol}//${domain}`,
  fe_images: (protocol: string, domain: string) => `${protocol}//${domain}:4200/assets/images/`,
  authenticationService: (protocol: string, domain: string) => `${protocol}//authentication.${domain}/api`,
  chessService: (protocol: string, domain: string) => `${protocol}//chess.${domain}/api`,
  adminService: (protocol: string, domain: string) => `${protocol}//admin.${domain}`,
  fitnessService: (protocol: string, domain: string) => `${protocol}//fitness.${domain}/api`,
  musicService: (protocol: string, domain: string) => `${protocol}//music.${domain}/api`,
  zipkinService: (protocol: string, domain: string) => `${protocol}//zipkin.${domain}/zipkin`,
  grafanaService: (protocol: string, domain: string) => `${protocol}//grafana.${domain}`,
  prometheusService: (protocol: string, domain: string) => `${protocol}//prometheus.${domain}`
};
