
import { 
  CalendarClock,
  Car, 
  Clock,
  Navigation
} from "lucide-react";

const StepItem = ({ 
  number, 
  icon, 
  title, 
  description 
}: { 
  number: number; 
  icon: React.ReactNode;
  title: string;
  description: string;
}) => {
  return (
    <div className="flex flex-col items-center text-center max-w-xs mx-auto">
      <div className="relative">
        <div className="w-16 h-16 rounded-full bg-carwash-blue flex items-center justify-center text-white mb-4">
          {icon}
        </div>
        <div className="absolute -top-2 -right-2 w-8 h-8 rounded-full bg-carwash-orange flex items-center justify-center text-white font-bold">
          {number}
        </div>
      </div>
      <h3 className="text-xl font-bold mb-2">{title}</h3>
      <p className="text-gray-600">{description}</p>
    </div>
  );
};

const HowItWorksSection = () => {
  return (
    <section id="como-funciona" className="py-16">
      <div className="container mx-auto px-4">
        <div className="text-center mb-16">
          <h2 className="text-3xl md:text-4xl font-bold mb-4 text-gradient">Como Funciona</h2>
          <p className="text-gray-600 max-w-2xl mx-auto">
            Agendar seu lava rápido nunca foi tão fácil! Siga os passos abaixo e tenha seu carro brilhando em poucos cliques.
          </p>
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 relative">
          <div className="hidden lg:block absolute top-1/3 left-0 right-0 h-0.5 bg-gray-200 -z-10"></div>
          
          <StepItem 
            number={1}
            icon={<CalendarClock className="h-8 w-8" />}
            title="Agende Online"
            description="Escolha dia e horário que melhor se adequam à sua rotina através do nosso sistema online."
          />
          
          <StepItem 
            number={2}
            icon={<Navigation className="h-8 w-8" />}
            title="Dirija até Nós"
            description="Chegue ao nosso estabelecimento no horário marcado. Estamos bem localizados e fácil de encontrar."
          />
          
          <StepItem 
            number={3}
            icon={<Clock className="h-8 w-8" />}
            title="Aguarde Brevemente"
            description="Nossa equipe especializada realizará o serviço com agilidade e qualidade."
          />
          
          <StepItem 
            number={4}
            icon={<Car className="h-8 w-8" />}
            title="Carro Impecável"
            description="Seu carro estará limpo e brilhando, pronto para você continuar seu dia."
          />
        </div>
      </div>
    </section>
  );
};

export default HowItWorksSection;
