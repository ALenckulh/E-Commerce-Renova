// Login Validation Schema
// Definir validações com Zod para formulário de login
// TODO: Implementar schema de validação com Zod

import { z } from 'zod';

export const loginSchema = z.object({
  // TODO: email, password fields
});

export type LoginFormData = z.infer<typeof loginSchema>;
