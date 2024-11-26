/**
 * Represents the payload of a JSON Web Token (JWT).
 *
 * @interface JwtPayload
 *
 * @property {string} iss - The issuer of the JWT.
 * @property {string} sub - The subject of the JWT.
 * @property {number} exp - The expiration time of the JWT (in seconds since epoch).
 * @property {number} iat - The issued at time of the JWT (in seconds since epoch).
 * @property {string[]} permissions - The permissions granted by the JWT.
 */
export interface JwtPayload {
  iss: string
  sub: string
  exp: number
  iat: number
  permissions: string[]
}
