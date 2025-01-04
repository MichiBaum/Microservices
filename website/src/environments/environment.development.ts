export const environment = {
  fe_images: (domain: string) => `http://${domain}:4200/assets/images/`,
  authenticationService: (domain: string) => `http://authentication.${domain}/api`,
  chessService: (domain: string) => `http://chess.${domain}/api`,
  adminService: (domain: string) => `http://admin.${domain}`,
  fitnessService: (domain: string) => `http://fitness.${domain}/api`,
  musicService: (domain: string) => `http://music.${domain}/api`
};
