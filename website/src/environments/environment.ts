export const environment = {
  fe_images: (domain: string) => `https://${domain}/assets/images/`,
  authenticationService: (domain: string) => `https://authentication.${domain}/api`,
  chessService: (domain: string) => `https://chess.${domain}/api`,
  adminService: (domain: string) => `https://admin.${domain}`,
  fitnessService: (domain: string) => `https://fitness.${domain}/api`,
  musicService: (domain: string) => `https://music.${domain}/api`
};
