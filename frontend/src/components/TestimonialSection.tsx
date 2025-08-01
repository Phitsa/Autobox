
import { Card, CardContent } from "@/components/ui/card";

interface TestimonialProps {
  name: string;
  position: string;
  content: string;
  image: string;
}

const TestimonialCard = ({ name, position, content, image }: TestimonialProps) => {
  return (
    <Card className="bg-white shadow-lg hover:shadow-xl transition-shadow">
      <CardContent className="p-6">
        <div className="flex items-center mb-4">
          <div className="w-12 h-12 rounded-full overflow-hidden mr-4">
            <img
              src={image}
              alt={name}
              className="w-full h-full object-cover"
            />
          </div>
          <div>
            <h4 className="font-bold text-gray-900">{name}</h4>
            <p className="text-sm text-gray-600">{position}</p>
          </div>
        </div>
        <p className="text-gray-600 italic">"{content}"</p>
        <div className="mt-4 flex">
          {[1, 2, 3, 4, 5].map((star) => (
            <svg 
              key={star}
              className="w-5 h-5 text-yellow-500" 
              fill="currentColor" 
              viewBox="0 0 20 20"
            >
              <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
            </svg>
          ))}
        </div>
      </CardContent>
    </Card>
  );
};

const TestimonialSection = () => {
  return (
    <section className="py-16 bg-gray-50">
      <div className="container mx-auto px-4">
        <div className="text-center mb-12">
          <h2 className="text-3xl md:text-4xl font-bold mb-4 text-gradient">O Que Dizem Nossos Clientes</h2>
          <p className="text-gray-600 max-w-2xl mx-auto">
            A satisfação dos nossos clientes é a nossa maior recompensa. Veja o que estão dizendo sobre nossos serviços.
          </p>
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          <TestimonialCard
            name="Mariana Silva"
            position="Cliente desde 2020"
            content="Excelente serviço! Meu carro fica sempre impecável e o atendimento é muito cordial. Super recomendo a Auto Box Car para quem busca qualidade e bom preço."
            image="https://randomuser.me/api/portraits/women/44.jpg"
          />
          
          <TestimonialCard
            name="Rafael Oliveira"
            position="Cliente desde 2018"
            content="Já testei diversos lava-rápidos, mas nenhum se compara à Auto Box Car. A agilidade no serviço sem perder a qualidade é o diferencial. Meu carro sempre sai como novo!"
            image="https://randomuser.me/api/portraits/men/32.jpg"
          />
          
          <TestimonialCard
            name="Camila Santos"
            position="Cliente desde 2021"
            content="O sistema de agendamento online é super prático! Não preciso mais esperar para ser atendida. Chego no horário marcado e meu carro é lavado rapidamente. Serviço nota 10!"
            image="https://randomuser.me/api/portraits/women/68.jpg"
          />
        </div>
      </div>
    </section>
  );
};

export default TestimonialSection;
