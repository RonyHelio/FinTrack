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

type Cartao = { id: string; nome: string; limite: number; bandeira: string };

export default function CartoesScreen() {
  const navigation = useNavigation();
  const { showNotification } = useNotification();
  const [cartoes, setCartoes] = useState<Cartao[]>([
    { id: "1", nome: "Cartão Nubank", limite: 4500, bandeira: "Mastercard" },
    { id: "2", nome: "Cartão Itaú", limite: 8000, bandeira: "Visa" },
  ]);

  const [novoNome, setNovoNome] = useState("");
  const [novoLimite, setNovoLimite] = useState("");

  function handleSalvar() {
    if (!novoNome || !novoLimite) {
      showNotification("Preencha os campos obrigatórios", "warning");
      return;
    }
    
    setCartoes([
      ...cartoes,
      {
        id: Date.now().toString(),
        nome: novoNome,
        limite: parseFloat(novoLimite),
        bandeira: "Mastercard",
      },
    ]);
    
    showNotification("Cartão cadastrado com sucesso!");
    setNovoNome("");
    setNovoLimite("");
  }

  return (
    <SafeAreaView className="flex-1 bg-dark-bg" edges={["top"]}>
      <View className="flex-row items-center px-6 pt-4 pb-4 border-b border-dark-border">
        <TouchableOpacity onPress={() => navigation.goBack()} className="mr-4">
          <Ionicons name="arrow-back" size={24} color="#F8FAFC" />
        </TouchableOpacity>
        <Text className="text-dark-text text-xl" style={{ fontFamily: "Inter_600SemiBold" }}>
          Meus Cartões
        </Text>
      </View>

      <ScrollView className="flex-1 px-6 pt-4">
        {cartoes.map((c) => (
          <View key={c.id} className="bg-dark-card p-4 rounded-xl mb-4 border border-dark-border flex-row justify-between items-center">
            <View className="flex-row items-center">
              <View className="w-10 h-10 bg-primary-950 rounded-full items-center justify-center mr-3">
                <Ionicons name="card-outline" size={20} color="#818CF8" />
              </View>
              <View>
                <Text className="text-dark-text text-base" style={{ fontFamily: "Inter_600SemiBold" }}>{c.nome}</Text>
                <Text className="text-dark-muted text-xs" style={{ fontFamily: "Inter_400Regular" }}>{c.bandeira}</Text>
              </View>
            </View>
            <View className="items-end">
                <Text className="text-dark-muted text-xs" style={{ fontFamily: "Inter_400Regular" }}>Limite</Text>
                <Text className="text-dark-text text-sm" style={{ fontFamily: "Inter_600SemiBold" }}>
                {formatCurrency(c.limite)}
                </Text>
            </View>
          </View>
        ))}

        <View className="bg-dark-card p-5 rounded-2xl border border-dark-border mt-4 mb-8">
          <Text className="text-dark-text text-lg mb-4" style={{ fontFamily: "Inter_600SemiBold" }}>
            Adicionar Cartão
          </Text>

          <Text className="text-dark-text text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>Nome do Cartão</Text>
          <TextInput
            className="bg-dark-bg border border-dark-border rounded-xl px-4 h-12 text-dark-text mb-4"
            placeholder="Ex: Cartão Inter"
            placeholderTextColor="#94A3B8"
            value={novoNome}
            onChangeText={setNovoNome}
          />

          <Text className="text-dark-text text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>Limite de Crédito</Text>
          <TextInput
            className="bg-dark-bg border border-dark-border rounded-xl px-4 h-12 text-dark-text mb-6"
            placeholder="0.00"
            placeholderTextColor="#94A3B8"
            keyboardType="numeric"
            value={novoLimite}
            onChangeText={setNovoLimite}
          />

          <TouchableOpacity
            className="w-full h-12 bg-primary-600 rounded-xl items-center justify-center"
            onPress={handleSalvar}
          >
            <Text className="text-white text-base" style={{ fontFamily: "Inter_600SemiBold" }}>
              Salvar Cartão
            </Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}
