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

type Orcamento = { id: string; categoria: string; valorPlanejado: number; valorGasto: number };

export default function OrcamentosScreen() {
  const navigation = useNavigation();
  const { showNotification } = useNotification();
  const [orcamentos, setOrcamentos] = useState<Orcamento[]>([
    { id: "1", categoria: "Alimentação", valorPlanejado: 1500, valorGasto: 800 },
    { id: "2", categoria: "Transporte", valorPlanejado: 500, valorGasto: 450 },
  ]);

  const [novaCategoria, setNovaCategoria] = useState("");
  const [novoValor, setNovoValor] = useState("");

  function handleSalvar() {
    if (!novaCategoria || !novoValor) {
      showNotification("Preencha os campos obrigatórios", "warning");
      return;
    }
    
    setOrcamentos([
      ...orcamentos,
      {
        id: Date.now().toString(),
        categoria: novaCategoria,
        valorPlanejado: parseFloat(novoValor),
        valorGasto: 0,
      },
    ]);
    
    showNotification("Orçamento cadastrado com sucesso!");
    setNovaCategoria("");
    setNovoValor("");
  }

  return (
    <SafeAreaView className="flex-1 bg-dark-bg" edges={["top"]}>
      <View className="flex-row items-center px-6 pt-4 pb-4 border-b border-dark-border">
        <TouchableOpacity onPress={() => navigation.goBack()} className="mr-4">
          <Ionicons name="arrow-back" size={24} color="#F8FAFC" />
        </TouchableOpacity>
        <Text className="text-dark-text text-xl" style={{ fontFamily: "Inter_600SemiBold" }}>
          Meus Orçamentos
        </Text>
      </View>

      <ScrollView className="flex-1 px-6 pt-4">
        {orcamentos.map((o) => {
            const percent = (o.valorGasto / o.valorPlanejado) * 100;
            const isDanger = percent > 90;
            return (
              <View key={o.id} className="bg-dark-card p-4 rounded-xl mb-4 border border-dark-border">
                <View className="flex-row justify-between mb-2">
                  <Text className="text-dark-text text-base" style={{ fontFamily: "Inter_600SemiBold" }}>{o.categoria}</Text>
                  <Text className="text-dark-text text-sm" style={{ fontFamily: "Inter_500Medium" }}>
                    {formatCurrency(o.valorGasto)} / {formatCurrency(o.valorPlanejado)}
                  </Text>
                </View>
                <View className="h-2 bg-dark-surface rounded-full overflow-hidden">
                    <View
                        className={`h-2 rounded-full ${isDanger ? "bg-danger-500" : "bg-primary-500"}`}
                        style={{ width: `${Math.min(percent, 100)}%` }}
                    />
                </View>
              </View>
            );
        })}

        <View className="bg-dark-card p-5 rounded-2xl border border-dark-border mt-4 mb-8">
          <Text className="text-dark-text text-lg mb-4" style={{ fontFamily: "Inter_600SemiBold" }}>
            Adicionar Orçamento
          </Text>

          <Text className="text-dark-text text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>Categoria</Text>
          <TextInput
            className="bg-dark-bg border border-dark-border rounded-xl px-4 h-12 text-dark-text mb-4"
            placeholder="Ex: Lazer"
            placeholderTextColor="#94A3B8"
            value={novaCategoria}
            onChangeText={setNovaCategoria}
          />

          <Text className="text-dark-text text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>Valor Limite</Text>
          <TextInput
            className="bg-dark-bg border border-dark-border rounded-xl px-4 h-12 text-dark-text mb-6"
            placeholder="0.00"
            placeholderTextColor="#94A3B8"
            keyboardType="numeric"
            value={novoValor}
            onChangeText={setNovoValor}
          />

          <TouchableOpacity
            className="w-full h-12 bg-primary-600 rounded-xl items-center justify-center"
            onPress={handleSalvar}
          >
            <Text className="text-white text-base" style={{ fontFamily: "Inter_600SemiBold" }}>
              Salvar Orçamento
            </Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}
