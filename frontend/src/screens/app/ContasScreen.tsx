import React, { useState } from "react";
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  TextInput,
  Platform,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { useNavigation } from "@react-navigation/native";
import { useNotification } from "../../contexts/NotificationContext";
import { formatCurrency } from "../../utils/format";

type Conta = { id: string; nome: string; saldo: number; tipo: string };

export default function ContasScreen() {
  const navigation = useNavigation();
  const { showNotification } = useNotification();
  const [contas, setContas] = useState<Conta[]>([
    { id: "1", nome: "Nubank", saldo: 2500, tipo: "Corrente" },
    { id: "2", nome: "Itaú", saldo: 10500.5, tipo: "Poupança" },
  ]);

  const [novaConta, setNovaConta] = useState("");
  const [novoSaldo, setNovoSaldo] = useState("");

  function handleSalvar() {
    if (!novaConta || !novoSaldo) {
      showNotification("Preencha os campos obrigatórios", "warning");
      return;
    }
    
    setContas([
      ...contas,
      {
        id: Date.now().toString(),
        nome: novaConta,
        saldo: parseFloat(novoSaldo),
        tipo: "Corrente",
      },
    ]);
    
    showNotification("Conta cadastrada com sucesso!");
    setNovaConta("");
    setNovoSaldo("");
  }

  return (
    <SafeAreaView className="flex-1 bg-dark-bg" edges={["top"]}>
      {/* Header */}
      <View className="flex-row items-center px-6 pt-4 pb-4 border-b border-dark-border">
        <TouchableOpacity onPress={() => navigation.goBack()} className="mr-4">
          <Ionicons name="arrow-back" size={24} color="#F8FAFC" />
        </TouchableOpacity>
        <Text className="text-dark-text text-xl" style={{ fontFamily: "Inter_600SemiBold" }}>
          Minhas Contas
        </Text>
      </View>

      <ScrollView className="flex-1 px-6 pt-4">
        {contas.map((c) => (
          <View key={c.id} className="bg-dark-card p-4 rounded-xl mb-4 border border-dark-border flex-row justify-between items-center">
            <View className="flex-row items-center">
              <View className="w-10 h-10 bg-primary-950 rounded-full items-center justify-center mr-3">
                <Ionicons name="wallet-outline" size={20} color="#818CF8" />
              </View>
              <View>
                <Text className="text-dark-text text-base" style={{ fontFamily: "Inter_600SemiBold" }}>{c.nome}</Text>
                <Text className="text-dark-muted text-xs" style={{ fontFamily: "Inter_400Regular" }}>{c.tipo}</Text>
              </View>
            </View>
            <Text className="text-dark-text text-base" style={{ fontFamily: "Inter_600SemiBold" }}>
              {formatCurrency(c.saldo)}
            </Text>
          </View>
        ))}

        <View className="bg-dark-card p-5 rounded-2xl border border-dark-border mt-4 mb-8">
          <Text className="text-dark-text text-lg mb-4" style={{ fontFamily: "Inter_600SemiBold" }}>
            Adicionar Conta
          </Text>

          <Text className="text-dark-text text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>Nome da Conta</Text>
          <TextInput
            className="bg-dark-bg border border-dark-border rounded-xl px-4 h-12 text-dark-text mb-4"
            placeholder="Ex: Banco do Brasil"
            placeholderTextColor="#94A3B8"
            value={novaConta}
            onChangeText={setNovaConta}
          />

          <Text className="text-dark-text text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>Saldo Inicial</Text>
          <TextInput
            className="bg-dark-bg border border-dark-border rounded-xl px-4 h-12 text-dark-text mb-6"
            placeholder="0.00"
            placeholderTextColor="#94A3B8"
            keyboardType="numeric"
            value={novoSaldo}
            onChangeText={setNovoSaldo}
          />

          <TouchableOpacity
            className="w-full h-12 bg-primary-600 rounded-xl items-center justify-center"
            onPress={handleSalvar}
          >
            <Text className="text-white text-base" style={{ fontFamily: "Inter_600SemiBold" }}>
              Salvar Conta
            </Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}
