import React, { useState } from "react";
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  TextInput,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { useNavigation } from "@react-navigation/native";
import { useNotification } from "../../contexts/NotificationContext";
import { formatCurrency } from "../../utils/format";

type Meta = { id: string; nome: string; valorAlvo: number; valorAtual: number; dataFim: string };

export default function MetasScreen() {
  const navigation = useNavigation();
  const { showNotification } = useNotification();
  const [metas, setMetas] = useState<Meta[]>([
    { id: "1", nome: "Viagem de Férias", valorAlvo: 10000, valorAtual: 3500, dataFim: "15/12/2026" },
    { id: "2", nome: "Reserva de Emergência", valorAlvo: 20000, valorAtual: 18000, dataFim: "01/01/2027" },
  ]);

  const [novoNome, setNovoNome] = useState("");
  const [novoValorAlvo, setNovoValorAlvo] = useState("");
  const [novaData, setNovaData] = useState("");

  function handleSalvar() {
    if (!novoNome || !novoValorAlvo || !novaData) {
      showNotification("Preencha os campos obrigatórios", "warning");
      return;
    }
    
    setMetas([
      ...metas,
      {
        id: Date.now().toString(),
        nome: novoNome,
        valorAlvo: parseFloat(novoValorAlvo),
        valorAtual: 0,
        dataFim: novaData,
      },
    ]);
    
    showNotification("Meta cadastrada com sucesso!");
    setNovoNome("");
    setNovoValorAlvo("");
    setNovaData("");
  }

  return (
    <SafeAreaView className="flex-1 bg-dark-bg" edges={["top"]}>
      <View className="flex-row items-center px-6 pt-4 pb-4 border-b border-dark-border">
        <TouchableOpacity onPress={() => navigation.goBack()} className="mr-4">
          <Ionicons name="arrow-back" size={24} color="#F8FAFC" />
        </TouchableOpacity>
        <Text className="text-dark-text text-xl" style={{ fontFamily: "Inter_600SemiBold" }}>
          Minhas Metas
        </Text>
      </View>

      <ScrollView className="flex-1 px-6 pt-4">
        {metas.map((m) => {
            const percent = (m.valorAtual / m.valorAlvo) * 100;
            return (
              <View key={m.id} className="bg-dark-card p-4 rounded-xl mb-4 border border-dark-border">
                <View className="flex-row justify-between mb-1">
                  <Text className="text-dark-text text-base" style={{ fontFamily: "Inter_600SemiBold" }}>{m.nome}</Text>
                </View>
                <Text className="text-dark-muted text-xs mb-3" style={{ fontFamily: "Inter_400Regular" }}>
                    Prazo: {m.dataFim}
                </Text>
                <View className="flex-row justify-between mb-2">
                  <Text className="text-dark-text text-sm" style={{ fontFamily: "Inter_500Medium" }}>
                    {formatCurrency(m.valorAtual)} / {formatCurrency(m.valorAlvo)}
                  </Text>
                  <Text className="text-success-500 text-sm" style={{ fontFamily: "Inter_600SemiBold" }}>
                    {percent.toFixed(0)}%
                  </Text>
                </View>
                <View className="h-2 bg-dark-surface rounded-full overflow-hidden">
                    <View
                        className="h-2 bg-success-500 rounded-full"
                        style={{ width: `${Math.min(percent, 100)}%` }}
                    />
                </View>
              </View>
            );
        })}

        <View className="bg-dark-card p-5 rounded-2xl border border-dark-border mt-4 mb-8">
          <Text className="text-dark-text text-lg mb-4" style={{ fontFamily: "Inter_600SemiBold" }}>
            Adicionar Meta
          </Text>

          <Text className="text-dark-text text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>Nome da Meta</Text>
          <TextInput
            className="bg-dark-bg border border-dark-border rounded-xl px-4 h-12 text-dark-text mb-4"
            placeholder="Ex: Comprar Carro"
            placeholderTextColor="#94A3B8"
            value={novoNome}
            onChangeText={setNovoNome}
          />

          <Text className="text-dark-text text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>Valor Alvo</Text>
          <TextInput
            className="bg-dark-bg border border-dark-border rounded-xl px-4 h-12 text-dark-text mb-4"
            placeholder="0.00"
            placeholderTextColor="#94A3B8"
            keyboardType="numeric"
            value={novoValorAlvo}
            onChangeText={setNovoValorAlvo}
          />

          <Text className="text-dark-text text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>Data Limite</Text>
          <TextInput
            className="bg-dark-bg border border-dark-border rounded-xl px-4 h-12 text-dark-text mb-6"
            placeholder="DD/MM/AAAA"
            placeholderTextColor="#94A3B8"
            value={novaData}
            onChangeText={setNovaData}
          />

          <TouchableOpacity
            className="w-full h-12 bg-primary-600 rounded-xl items-center justify-center"
            onPress={handleSalvar}
          >
            <Text className="text-white text-base" style={{ fontFamily: "Inter_600SemiBold" }}>
              Salvar Meta
            </Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}
