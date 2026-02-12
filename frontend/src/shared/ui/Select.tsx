// Select Component
// Componente reutilizável de select/dropdown
// TODO: Implementar com label, options, placeholder, error message

interface SelectProps {
  // TODO: label, options, placeholder, error, value, onChange, className
}

export const Select = ({ ...props }: SelectProps) => {
  return (
    <select {...props}>
      {/* TODO: Implementar select com opções */}
    </select>
  );
};
