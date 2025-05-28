
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { CarFront } from "lucide-react";

interface ServiceCardProps {
  title: string;
  description: string;
  price: string;
  popular?: boolean;
  icon: React.ReactNode;
}

const ServiceCard = ({ title, description, price, popular, icon }: ServiceCardProps) => {
  return (
    <Card className="relative overflow-hidden transition-all hover:-translate-y-1 hover:shadow-xl">
      {popular && (
        <Badge className="absolute top-3 right-3 bg-carwash-orange">
          Mais Popular
        </Badge>
      )}
      <CardContent className="pt-6">
        <div className="p-4 bg-blue-50 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-5 text-carwash-blue">
          {icon}
        </div>
        <h3 className="text-xl font-bold text-center mb-2">{title}</h3>
        <p className="text-gray-600 text-center mb-4">{description}</p>
        <p className="text-carwash-blue font-bold text-2xl text-center">{price}</p>
      </CardContent>
    </Card>
  );
};

const ServicesSection = () => {
  return (
    <section id="servicos" className="py-16 bg-gray-50">
      <div className="container mx-auto px-4">
        <div className="text-center mb-12">
          <h2 className="text-3xl md:text-4xl font-bold mb-4 text-gradient">Nossos Serviços</h2>
          <p className="text-gray-600 max-w-2xl mx-auto">
            Oferecemos uma variedade de serviços de lavagem para manter seu veículo sempre impecável.
            Escolha o que melhor atende às suas necessidades.
          </p>
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <ServiceCard 
            title="Lavagem Simples"
            description="Limpeza externa completa, incluindo rodas e pneus."
            price="R$35,00"
            icon={<CarFront size={32} />}
          />
          
          <ServiceCard 
            title="Lavagem Completa"
            description="Limpeza externa e interna, incluindo aspiração e painel."
            price="R$60,00"
            popular={true}
            icon={<CarFront size={32} />}
          />
          
          <ServiceCard 
            title="Enceramento"
            description="Protege a pintura e garante mais brilho e durabilidade."
            price="R$80,00"
            icon={<CarFront size={32} />}
          />
          
          <ServiceCard 
            title="Lavagem Premium"
            description="O pacote completo com hidratação de couro e cera especial."
            price="R$120,00"
            icon={<CarFront size={32} />}
          />
        </div>
      </div>
    </section>
  );
};

export default ServicesSection;
