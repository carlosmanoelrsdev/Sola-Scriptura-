export type AuthUser = {
  id: string
  name: string
  email: string
  role: string
}

export type LoginInput = {
  email: string
  password: string
}

export type RegisterInput = {
  name: string
  email: string
  password: string
}

export type LoginResponse = {
  token: string
  tokenType: string
  expiresIn: number
  user: AuthUser
}

export type RegisterResponse = AuthUser & {
  createdAt: string
}
