import React, { useState, useCallback } from "react";
import {
  View,
  Text,
  FlatList,
  ActivityIndicator,
  RefreshControl,
  TouchableOpacity,
  Alert,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { useFocusEffect, useNavigation } from "@react-navigation/native";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { transacaoService } from "../../services/transacaoService";
import { formatCurrency, formatDate } from "../../utils/format";
import type { TransacaoResponse, TransacaoFiltro, ApiError } from "../../types";
import type { RootStackParamList } from "../../navigation/RootNavigator";

type NavProp = NativeStackNavigationProp<RootStackParamList>;

const MESES = [
  "Todos", "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
  "Jul", "Ago", "Set", "Out", "Nov", "Dez",
];

const TIPOS = ["Todos", "Receita", "Despesa"];

export default function TransacoesScreen() {
  const navigation = useNavigation<NavProp>();
  const [transacoes, setTransacoes] = useState<TransacaoResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  // Filtros
  const [mesSelecionado, setMesSelecionado] = useState(0); // 0 = Todos
  const [tipoSelecionado, setTipoSelecionado] = useState(0); // 0 = Todos
  const anoAtual = new Date().getFullYear();

  const loadTransacoes = useCallback(async () => {
    try {
      const filtros: TransacaoFiltro = {};
      if (mesSelecionado > 0) {
        filtros.mes = mesSelecionado;
        filtros.ano = anoAtual;
      }
      if (tipoSelecionado === 1) filtros.tipo = "receita";
      if (tipoSelecionado === 2) filtros.tipo = "despesa";

      const hasFilter = Object.keys(filtros).length > 0;
      const data = hasFilter
        ? await transacaoService.filtrar(filtros)
        : await transacaoService.listar();
      setTransacoes(data);
    } catch (error) {
      console.error("Erro ao carregar transações:", error);
    } finally {
      setIsLoading(false);
      setRefreshing(false);
    }
  }, [mesSelecionado, tipoSelecionado, anoAtual]);

  useFocusEffect(
    useCallback(() => {
      setIsLoading(true);
      loadTransacoes();
    }, [loadTransacoes])
  );

  function onRefresh() {
    setRefreshing(true);
    loadTransacoes();
  }

  async function handleDelete(id: string) {
    Alert.alert("Excluir transação", "Tem certeza que deseja excluir?", [
      { text: "Cancelar", style: "cancel" },
      {
        text: "Excluir",
        style: "destructive",
        onPress: async () => {
          try {
            await transacaoService.deletar(id);
            setTransacoes((prev) => prev.filter((t) => t.id !== id));
          } catch (error) {
            const apiError = error as ApiError;
            Alert.alert("Erro", apiError.mensagem || "Não foi possível excluir.");
          }
        },
      },
    ]);
  }

  function renderItem({ item }: { item: TransacaoResponse }) {
    const isReceita = item.tipo === "receita";
    return (
      <TouchableOpacity
        className="bg-dark-card rounded-2xl p-4 mb-3 border border-dark-border"
        activeOpacity={0.7}
        onPress={() => navigation.navigate("TransacaoForm", { transacao: item })}
        onLongPress={() => handleDelete(item.id)}
      >
        <View className="flex-row items-center">
          <View
            className={`w-11 h-11 rounded-xl items-center justify-center mr-3 ${
              isReceita ? "bg-success-50" : "bg-danger-50"
            }`}
          >
            <Ionicons
              name={isReceita ? "arrow-up" : "arrow-down"}
              size={20}
              color={isReceita ? "#10B981" : "#EF4444"}
            />
          </View>
          <View className="flex-1">
            <Text className="text-dark-text text-sm" style={{ fontFamily: "Inter_500Medium" }} numberOfLines={1}>
              {item.descricao}
            </Text>
            <Text className="text-dark-muted text-xs mt-0.5" style={{ fontFamily: "Inter_400Regular" }}>
              {item.categoriaNome} • {formatDate(item.data)}
            </Text>
          </View>
          <Text
            className={`text-sm ${isReceita ? "text-success-500" : "text-danger-500"}`}
            style={{ fontFamily: "Inter_600SemiBold" }}
          >
            {isReceita ? "+" : "-"}{formatCurrency(item.valor)}
          </Text>
        </View>
      </TouchableOpacity>
    );
  }

  return (
    <SafeAreaView className="flex-1 bg-dark-bg" edges={["top"]}>
      <View className="flex-1 px-6 pt-4">
        {/* ─── Header ────────────────────────────────────────────── */}
        <View className="flex-row items-center justify-between mb-4">
          <Text className="text-dark-text text-2xl" style={{ fontFamily: "Inter_700Bold" }}>
            Histórico
          </Text>
          <TouchableOpacity
            className="w-10 h-10 rounded-full bg-primary-600 items-center justify-center"
            onPress={() => navigation.navigate("TransacaoForm")}
            activeOpacity={0.8}
          >
            <Ionicons name="add" size={22} color="#FFFFFF" />
          </TouchableOpacity>
        </View>

        {/* ─── Filtro por Tipo ────────────────────────────────────── */}
        <View className="flex-row gap-2 mb-3">
          {TIPOS.map((label, idx) => (
            <TouchableOpacity
              key={label}
              className={`flex-1 py-2.5 rounded-xl items-center ${
                tipoSelecionado === idx ? "bg-primary-600" : "bg-dark-card border border-dark-border"
              }`}
              onPress={() => setTipoSelecionado(idx)}
              activeOpacity={0.7}
            >
              <Text
                className={`text-xs ${tipoSelecionado === idx ? "text-white" : "text-dark-muted"}`}
                style={{ fontFamily: "Inter_500Medium" }}
              >
                {label}
              </Text>
            </TouchableOpacity>
          ))}
        </View>

        {/* ─── Filtro por Mês (scroll horizontal) ────────────────── */}
        <FlatList
          horizontal
          data={MESES}
          keyExtractor={(item) => item}
          showsHorizontalScrollIndicator={false}
          className="mb-4 max-h-10"
          renderItem={({ item, index }) => (
            <TouchableOpacity
              className={`px-4 py-2 rounded-lg mr-2 ${
                mesSelecionado === index ? "bg-primary-600" : "bg-dark-card border border-dark-border"
              }`}
              onPress={() => setMesSelecionado(index)}
              activeOpacity={0.7}
            >
              <Text
                className={`text-xs ${mesSelecionado === index ? "text-white" : "text-dark-muted"}`}
                style={{ fontFamily: "Inter_500Medium" }}
              >
                {item}
              </Text>
            </TouchableOpacity>
          )}
        />

        {/* ─── Lista / Loading / Empty ───────────────────────────── */}
        {isLoading && !refreshing ? (
          <View className="flex-1 items-center justify-center">
            <ActivityIndicator size="large" color="#6366F1" />
          </View>
        ) : transacoes.length === 0 ? (
          <View className="flex-1 items-center justify-center">
            <View className="bg-dark-card rounded-2xl p-8 items-center border border-dark-border w-full">
              <Text className="text-5xl mb-4">💸</Text>
              <Text className="text-dark-text text-lg mb-2" style={{ fontFamily: "Inter_600SemiBold" }}>
                Nenhuma transação
              </Text>
              <Text className="text-dark-muted text-sm text-center" style={{ fontFamily: "Inter_400Regular" }}>
                {mesSelecionado > 0 || tipoSelecionado > 0
                  ? "Nenhuma transação com esses filtros"
                  : "Toque no + para adicionar\nsua primeira transação"}
              </Text>
            </View>
          </View>
        ) : (
          <FlatList
            data={transacoes}
            keyExtractor={(item) => item.id}
            renderItem={renderItem}
            showsVerticalScrollIndicator={false}
            refreshControl={
              <RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor="#6366F1" />
            }
            contentContainerStyle={{ paddingBottom: 16 }}
          />
        )}
      </View>
    </SafeAreaView>
  );
}
