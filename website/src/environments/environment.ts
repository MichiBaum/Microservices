export const environment = {
    base_url: (domain: string) => `https://${domain}`,
    fe_images: (domain: string) => `https://${domain}/assets/images/`,
    authenticationService: (domain: string) => `https://authentication.${domain}/api`,
    chessService: (domain: string) => `https://chess.${domain}/api`,
    adminService: (domain: string) => `https://admin.${domain}`,
    fitnessService: (domain: string) => `https://fitness.${domain}/api`,
    musicService: (domain: string) => `https://music.${domain}/api`,
    zipkinService: (domain: string) => `https://zipkin.${domain}/zipkin`,
    grafanaService: (domain: string) => `https://garafana.${domain}`,
    prometheusService: (domain: string) => `https://prometheus.${domain}`
};
