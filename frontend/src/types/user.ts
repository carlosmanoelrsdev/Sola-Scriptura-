export type UserProfile = {
  id: string
  name: string
  email: string
  role: string
  createdAt: string
  updatedAt: string
}

export type UpdateUserProfileInput = {
  name: string
  email: string
}
