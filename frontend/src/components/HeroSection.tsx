
import { Button } from "@/components/ui/button";
import { CalendarClock } from "lucide-react";

const HeroSection = () => {
  return (
    <section id="inicio" className="relative bg-gradient-to-r from-blue-900 to-blue-700 pt-24 pb-16 md:pt-32 md:pb-24">
      <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1607860108855-64acf2078ed9?ixlib=rb-1.2.1&q=80&fm=jpg&crop=entropy&cs=tinysrgb&dl=oscar-sutton-Y1NBBydL2BU-unsplash.jpg')] bg-cover bg-center opacity-20"></div>
      
      <div className="container mx-auto px-6 relative z-10">
        <div className="flex flex-col md:flex-row items-center">
          <div className="md:w-1/2 text-center md:text-left mb-10 md:mb-0">
            <h1 className="text-4xl md:text-5xl font-bold text-white mb-6 leading-tight">
              Auto Box Car<br/>
              <span className="text-yellow-300">Lava Rápido</span>
            </h1>
            <p className="text-white/90 text-lg mb-8 max-w-lg mx-auto md:mx-0">
              Seu carro merece o melhor tratamento! Agende seu horário agora.
            </p>
            <Button size="lg" className="bg-carwash-orange hover:bg-orange-600 text-white">
              <CalendarClock className="mr-2 h-5 w-5" />
              Agendar Agora
            </Button>
          </div>
          
          <div className="md:w-1/2">
            <div className="bg-white rounded-xl p-6 shadow-lg">
              <h3 className="text-2xl font-bold text-carwash-blue mb-4 text-center">Horário de Funcionamento</h3>
              <div className="space-y-3">
                <div className="flex justify-between border-b pb-2">
                  <span>Segunda - Sexta</span>
                  <span className="font-semibold">8:00 - 19:00</span>
                </div>
                <div className="flex justify-between border-b pb-2">
                  <span>Sábado</span>
                  <span className="font-semibold">8:00 - 17:00</span>
                </div>
                <div className="flex justify-between">
                  <span>Domingo</span>
                  <span className="font-semibold">9:00 - 14:00</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default HeroSection;
