
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { useToast } from "@/components/ui/use-toast";

const ContactSection = () => {
  const { toast } = useToast();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    toast({
      title: "Mensagem enviada!",
      description: "Entraremos em contato em breve.",
      duration: 3000,
    });
    // Reset form
    (e.target as HTMLFormElement).reset();
  };

  return (
    <section id="contato" className="py-16">
      <div className="container mx-auto px-4">
        <div className="text-center mb-12">
          <h2 className="text-3xl md:text-4xl font-bold mb-4 text-gradient">Fale Conosco</h2>
          <p className="text-gray-600 max-w-2xl mx-auto">
            Tem alguma dúvida ou sugestão? Entre em contato conosco! Estamos sempre à disposição para melhor atendê-lo.
          </p>
        </div>
        
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-10">
          <div className="bg-gray-50 p-8 rounded-lg shadow-md">
            <h3 className="text-2xl font-bold text-carwash-blue mb-6">Informações de Contato</h3>
            
            <div className="space-y-6">
              <div>
                <h4 className="font-semibold text-lg mb-2">Endereço</h4>
                <p className="text-gray-600">
                  Av. Principal, 1234<br />
                  Centro, São Paulo - SP<br />
                  CEP: 01234-567
                </p>
              </div>
              
              <div>
                <h4 className="font-semibold text-lg mb-2">Telefone</h4>
                <p className="text-gray-600">
                  (11) 5555-1234<br />
                  (11) 98765-4321
                </p>
              </div>
              
              <div>
                <h4 className="font-semibold text-lg mb-2">Email</h4>
                <p className="text-gray-600">
                  contato@autoboxcar.com.br<br />
                  agendamento@autoboxcar.com.br
                </p>
              </div>
              
              <div>
                <h4 className="font-semibold text-lg mb-2">Horário de Atendimento</h4>
                <p className="text-gray-600">
                  Segunda - Sexta: 8:00 - 19:00<br />
                  Sábado: 8:00 - 17:00<br />
                  Domingo: 9:00 - 14:00
                </p>
              </div>
            </div>
            
            <div className="mt-8">
              <iframe 
                src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3657.1976900160257!2d-46.6528357!3d-23.5636808!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x94ce59c8da0aa315%3A0xd59f9431f2c9776a!2sAv.%20Paulista%2C%20S%C3%A3o%20Paulo%20-%20SP!5e0!3m2!1spt-BR!2sbr!4v1684972266373!5m2!1spt-BR!2sbr" 
                className="w-full h-64 rounded-lg border-0" 
                allowFullScreen 
                loading="lazy" 
                title="Localização Auto Box Car"
              ></iframe>
            </div>
          </div>
          
          <div>
            <form onSubmit={handleSubmit} className="space-y-6">
              <div>
                <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-1">Nome</label>
                <Input id="name" placeholder="Seu nome completo" required />
              </div>
              
              <div>
                <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                <Input id="email" type="email" placeholder="seu.email@exemplo.com" required />
              </div>
              
              <div>
                <label htmlFor="phone" className="block text-sm font-medium text-gray-700 mb-1">Telefone</label>
                <Input id="phone" placeholder="(00) 00000-0000" />
              </div>
              
              <div>
                <label htmlFor="subject" className="block text-sm font-medium text-gray-700 mb-1">Assunto</label>
                <Input id="subject" placeholder="Motivo do contato" required />
              </div>
              
              <div>
                <label htmlFor="message" className="block text-sm font-medium text-gray-700 mb-1">Mensagem</label>
                <Textarea id="message" placeholder="Digite sua mensagem aqui..." rows={6} required />
              </div>
              
              <Button type="submit" className="w-full bg-carwash-blue hover:bg-blue-700">
                Enviar Mensagem
              </Button>
            </form>
          </div>
        </div>
      </div>
    </section>
  );
};

export default ContactSection;
