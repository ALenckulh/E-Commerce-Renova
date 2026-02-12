// Register Validation Schema
// Definir validações com Zod para formulário de registro
// TODO: Implementar schema de validação com Zod

import { z } from 'zod';

export const registerSchema = z.object({
  // TODO: name, email, password, confirmPassword fields
});

export type RegisterFormData = z.infer<typeof registerSchema>;
