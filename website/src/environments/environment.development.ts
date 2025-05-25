export const environment = {
    base_url: (domain: string) => `http://${domain}`,
    fe_images: (domain: string) => `http://${domain}:4200/assets/images/`,
    authenticationService: (domain: string) => `http://authentication.${domain}/api`,
    chessService: (domain: string) => `http://chess.${domain}/api`,
    adminService: (domain: string) => `http://admin.${domain}`,
    fitnessService: (domain: string) => `http://fitness.${domain}/api`,
    musicService: (domain: string) => `http://music.${domain}/api`,
    zipkinService: (domain: string) => `http://zipkin.${domain}/zipkin`,
    grafanaService: (domain: string) => `http://grafana.${domain}`,
    prometheusService: (domain: string) => `http://prometheus.${domain}`
};
