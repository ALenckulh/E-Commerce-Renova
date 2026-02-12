// Button Component
// Componente reutilizável de botão
// TODO: Implementar com variantes (primary, secondary), sizes, estados (loading, disabled)

interface ButtonProps {
  // TODO: children, variant, size, onClick, disabled, loading, className
}

export const Button = ({ children, ...props }: ButtonProps) => {
  return (
    <button {...props}>
      {/* TODO: Implementar botão com variantes */}
      {children}
    </button>
  );
};
