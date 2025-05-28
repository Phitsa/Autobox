
import { Button } from "@/components/ui/button";

const AboutSection = () => {
  return (
    <section id="sobre" className="py-16 bg-gradient-to-r from-carwash-blue to-blue-700 text-white">
      <div className="container mx-auto px-4">
        <div className="flex flex-col lg:flex-row items-center gap-12">
          <div className="lg:w-1/2 order-2 lg:order-1">
            <h2 className="text-3xl md:text-4xl font-bold mb-6">Sobre a Auto Box Car</h2>
            <p className="text-white/90 mb-5 text-lg">
              Desde 2015, a Auto Box Car tem se dedicado a oferecer o melhor serviço de lavagem de veículos da região. 
              Nossa missão é proporcionar um atendimento de excelência, utilizando produtos de alta qualidade e tecnologia avançada.
            </p>
            <p className="text-white/90 mb-8 text-lg">
              Contamos com uma equipe de profissionais altamente treinados, comprometidos em entregar um resultado impecável. 
              Cada veículo é tratado com cuidado e atenção aos detalhes, garantindo um serviço rápido sem abrir mão da qualidade.
            </p>
            <div className="flex space-x-6">
              <div className="text-center">
                <span className="block text-4xl font-bold text-yellow-300">7+</span>
                <span className="text-sm text-white/80">Anos de Experiência</span>
              </div>
              
              <div className="text-center">
                <span className="block text-4xl font-bold text-yellow-300">15k+</span>
                <span className="text-sm text-white/80">Clientes Satisfeitos</span>
              </div>
              
              <div className="text-center">
                <span className="block text-4xl font-bold text-yellow-300">30+</span>
                <span className="text-sm text-white/80">Colaboradores</span>
              </div>
            </div>
            
            <Button variant="secondary" className="mt-8 bg-white text-carwash-blue hover:bg-gray-100">
              Conheça Nossa Equipe
            </Button>
          </div>
          
          <div className="lg:w-1/2 order-1 lg:order-2">
            <div className="relative">
              <img 
                src="https://images.unsplash.com/photo-1605618826115-fb9e775cf7ae?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80" 
                alt="Equipe Auto Box Car" 
                className="rounded-lg shadow-xl w-full object-cover h-[400px]"
              />
              <div className="absolute -bottom-6 -left-6 bg-carwash-orange p-5 rounded-lg rotate-3">
                <p className="text-xl font-bold">Compromisso com qualidade!</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default AboutSection;
