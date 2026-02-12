// Global Types
// Tipos compartilhados entre features
// TODO: User, AuthResponse, ApiResponse, PaginationResponse, etc

export interface AuthResponse {
  // TODO: token, user, expiresIn
}

export interface ApiResponse<T> {
  // TODO: data, message, success
}

export interface PaginationResponse<T> {
  // TODO: data, page, limit, total, totalPages
}
