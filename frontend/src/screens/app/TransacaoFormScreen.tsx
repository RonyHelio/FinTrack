import React, { useState, useEffect, useCallback } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ScrollView,
  Alert,
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { useNavigation, useRoute, RouteProp } from "@react-navigation/native";
import { transacaoService } from "../../services/transacaoService";
import { categoriaService } from "../../services/categoriaService";
import { todayISO } from "../../utils/format";
import type { CategoriaResponse, TransacaoRequest, ApiError } from "../../types";
import type { RootStackParamList } from "../../navigation/RootNavigator";

type FormRoute = RouteProp<RootStackParamList, "TransacaoForm">;

export default function TransacaoFormScreen() {
  const navigation = useNavigation();
  const route = useRoute<FormRoute>();
  const editando = route.params?.transacao;

  const [valor, setValor] = useState(editando ? String(editando.valor) : "");
  const [tipo, setTipo] = useState<"receita" | "despesa">(
    editando ? editando.tipo : "despesa"
  );
  const [categoriaId, setCategoriaId] = useState(editando?.categoriaId ?? "");
  const [data, setData] = useState(editando?.data ?? todayISO());
  const [descricao, setDescricao] = useState(editando?.descricao ?? "");

  const [categorias, setCategorias] = useState<CategoriaResponse[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [loadingCategorias, setLoadingCategorias] = useState(true);

  useEffect(() => {
    async function loadCategorias() {
      try {
        const cats = await categoriaService.listarMinhas();
        setCategorias(cats);
        if (!editando && cats.length > 0 && !categoriaId) {
          setCategoriaId(cats[0].id);
        }
      } catch (error) {
        console.error("Erro ao carregar categorias:", error);
      } finally {
        setLoadingCategorias(false);
      }
    }
    loadCategorias();
  }, []);

  function formatDataInput(text: string): string {
    const clean = text.replace(/[^0-9]/g, "");
    if (clean.length <= 4) return clean;
    if (clean.length <= 6) return `${clean.slice(0, 4)}-${clean.slice(4)}`;
    return `${clean.slice(0, 4)}-${clean.slice(4, 6)}-${clean.slice(6, 8)}`;
  }

  async function handleSubmit() {
    if (!valor.trim() || !categoriaId || !data.trim() || !descricao.trim()) {
      Alert.alert("Atenção", "Preencha todos os campos.");
      return;
    }

    const valorNum = parseFloat(valor.replace(",", "."));
    if (isNaN(valorNum) || valorNum <= 0) {
      Alert.alert("Atenção", "Informe um valor válido maior que zero.");
      return;
    }

    const dataRegex = /^\d{4}-\d{2}-\d{2}$/;
    if (!dataRegex.test(data)) {
      Alert.alert("Atenção", "Data inválida. Use o formato AAAA-MM-DD.");
      return;
    }

    setIsLoading(true);
    try {
      const payload: TransacaoRequest = {
        categoriaId,
        valor: valorNum,
        tipo,
        data,
        descricao: descricao.trim(),
      };

      if (editando) {
        await transacaoService.atualizar(editando.id, payload);
        Alert.alert("Sucesso", "Transação atualizada!", [
          { text: "OK", onPress: () => navigation.goBack() },
        ]);
      } else {
        await transacaoService.criar(payload);
        Alert.alert("Sucesso", "Transação criada!", [
          { text: "OK", onPress: () => navigation.goBack() },
        ]);
      }
    } catch (error) {
      const apiError = error as ApiError;
      Alert.alert("Erro", apiError.mensagem || "Não foi possível salvar a transação.");
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <SafeAreaView className="flex-1 bg-dark-bg" edges={["top"]}>
      <KeyboardAvoidingView
        className="flex-1"
        behavior={Platform.OS === "ios" ? "padding" : "height"}
      >
        <ScrollView className="flex-1 px-6 pt-4" keyboardShouldPersistTaps="handled">
          {/* ─── Header ────────────────────────────────────────────── */}
          <View className="flex-row items-center mb-6">
            <TouchableOpacity
              className="w-10 h-10 rounded-xl bg-dark-card border border-dark-border items-center justify-center mr-3"
              onPress={() => navigation.goBack()}
            >
              <Ionicons name="arrow-back" size={20} color="#F8FAFC" />
            </TouchableOpacity>
            <Text className="text-dark-text text-xl flex-1" style={{ fontFamily: "Inter_700Bold" }}>
              {editando ? "Editar transação" : "Nova transação"}
            </Text>
          </View>

          {/* ─── Tipo (receita / despesa) ─────────────────────────── */}
          <Text className="text-dark-muted text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>
            Tipo
          </Text>
          <View className="flex-row gap-3 mb-5">
            <TouchableOpacity
              className={`flex-1 py-3.5 rounded-xl items-center flex-row justify-center ${
                tipo === "receita" ? "bg-success-500" : "bg-dark-card border border-dark-border"
              }`}
              onPress={() => setTipo("receita")}
              activeOpacity={0.7}
            >
              <Ionicons
                name="arrow-up"
                size={18}
                color={tipo === "receita" ? "#FFFFFF" : "#94A3B8"}
              />
              <Text
                className={`ml-2 text-sm ${tipo === "receita" ? "text-white" : "text-dark-muted"}`}
                style={{ fontFamily: "Inter_600SemiBold" }}
              >
                Receita
              </Text>
            </TouchableOpacity>
            <TouchableOpacity
              className={`flex-1 py-3.5 rounded-xl items-center flex-row justify-center ${
                tipo === "despesa" ? "bg-danger-500" : "bg-dark-card border border-dark-border"
              }`}
              onPress={() => setTipo("despesa")}
              activeOpacity={0.7}
            >
              <Ionicons
                name="arrow-down"
                size={18}
                color={tipo === "despesa" ? "#FFFFFF" : "#94A3B8"}
              />
              <Text
                className={`ml-2 text-sm ${tipo === "despesa" ? "text-white" : "text-dark-muted"}`}
                style={{ fontFamily: "Inter_600SemiBold" }}
              >
                Despesa
              </Text>
            </TouchableOpacity>
          </View>

          {/* ─── Valor ───────────────────────────────────────────── */}
          <Text className="text-dark-muted text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>
            Valor (R$)
          </Text>
          <TextInput
            className="bg-dark-surface border border-dark-border rounded-xl px-4 py-3.5 text-dark-text text-lg mb-5"
            style={{ fontFamily: "Inter_600SemiBold" }}
            placeholder="0,00"
            placeholderTextColor="#64748B"
            keyboardType="decimal-pad"
            value={valor}
            onChangeText={setValor}
          />

          {/* ─── Categoria ───────────────────────────────────────── */}
          <Text className="text-dark-muted text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>
            Categoria
          </Text>
          {loadingCategorias ? (
            <ActivityIndicator color="#6366F1" className="my-4" />
          ) : (
            <ScrollView horizontal showsHorizontalScrollIndicator={false} className="mb-5 max-h-12">
              {categorias.map((cat) => (
                <TouchableOpacity
                  key={cat.id}
                  className={`flex-row items-center px-4 py-2.5 rounded-xl mr-2 ${
                    categoriaId === cat.id
                      ? "bg-primary-600"
                      : "bg-dark-card border border-dark-border"
                  }`}
                  onPress={() => setCategoriaId(cat.id)}
                  activeOpacity={0.7}
                >
                  <Text className="mr-1.5">{cat.icone}</Text>
                  <Text
                    className={`text-xs ${categoriaId === cat.id ? "text-white" : "text-dark-muted"}`}
                    style={{ fontFamily: "Inter_500Medium" }}
                  >
                    {cat.nome}
                  </Text>
                </TouchableOpacity>
              ))}
            </ScrollView>
          )}

          {/* ─── Data ────────────────────────────────────────────── */}
          <Text className="text-dark-muted text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>
            Data (AAAA-MM-DD)
          </Text>
          <TextInput
            className="bg-dark-surface border border-dark-border rounded-xl px-4 py-3.5 text-dark-text text-base mb-5"
            style={{ fontFamily: "Inter_400Regular" }}
            placeholder="2026-06-01"
            placeholderTextColor="#64748B"
            keyboardType="numeric"
            maxLength={10}
            value={data}
            onChangeText={(text) => setData(formatDataInput(text))}
          />

          {/* ─── Descrição ───────────────────────────────────────── */}
          <Text className="text-dark-muted text-sm mb-2" style={{ fontFamily: "Inter_500Medium" }}>
            Descrição
          </Text>
          <TextInput
            className="bg-dark-surface border border-dark-border rounded-xl px-4 py-3.5 text-dark-text text-base mb-8"
            style={{ fontFamily: "Inter_400Regular", minHeight: 80, textAlignVertical: "top" }}
            placeholder="Ex: Supermercado, Salário..."
            placeholderTextColor="#64748B"
            multiline
            numberOfLines={3}
            value={descricao}
            onChangeText={setDescricao}
          />

          {/* ─── Botão salvar ────────────────────────────────────── */}
          <TouchableOpacity
            className={`rounded-xl py-4 items-center flex-row justify-center mb-6 ${
              isLoading ? "bg-primary-800" : "bg-primary-500"
            }`}
            onPress={handleSubmit}
            disabled={isLoading}
            activeOpacity={0.8}
          >
            {isLoading ? (
              <ActivityIndicator color="#FFFFFF" />
            ) : (
              <>
                <Ionicons name={editando ? "checkmark" : "add"} size={20} color="#FFFFFF" />
                <Text className="text-white text-base ml-2" style={{ fontFamily: "Inter_600SemiBold" }}>
                  {editando ? "Salvar alterações" : "Criar transação"}
                </Text>
              </>
            )}
          </TouchableOpacity>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}
