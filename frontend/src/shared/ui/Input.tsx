// Input Component
// Componente reutilizável de input
// TODO: Implementar com label, placeholder, error message, validation

interface InputProps {
  // TODO: label, placeholder, error, value, onChange, type, className
}

export const Input = ({ ...props }: InputProps) => {
  return (
    <input {...props}>
      {/* TODO: Implementar input com validação */}
    </input>
  );
};
