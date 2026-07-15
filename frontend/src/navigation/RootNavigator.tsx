import React from "react";
import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { useAuth } from "../contexts/AuthContext";
import AuthStack from "./AuthStack";
import AppTabs from "./AppTabs";
import TransacaoFormScreen from "../screens/app/TransacaoFormScreen";
import ContasScreen from "../screens/app/ContasScreen";
import CartoesScreen from "../screens/app/CartoesScreen";
import OrcamentosScreen from "../screens/app/OrcamentosScreen";
import MetasScreen from "../screens/app/MetasScreen";
import { ActivityIndicator, View } from "react-native";
import type { TransacaoResponse } from "../types";

export type RootStackParamList = {
  Auth: undefined;
  App: undefined;
  TransacaoForm: { transacao?: TransacaoResponse } | undefined;
  Contas: undefined;
  Cartoes: undefined;
  Orcamentos: undefined;
  Metas: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();

export default function RootNavigator() {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <View className="flex-1 items-center justify-center bg-dark-bg">
        <ActivityIndicator size="large" color="#6366F1" />
      </View>
    );
  }

  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{ headerShown: false }}>
        {isAuthenticated ? (
          <>
            <Stack.Screen name="App" component={AppTabs} />
            <Stack.Screen
              name="TransacaoForm"
              component={TransacaoFormScreen}
              options={{
                presentation: "modal",
                animation: "slide_from_bottom",
              }}
            />
            <Stack.Screen name="Contas" component={ContasScreen} />
            <Stack.Screen name="Cartoes" component={CartoesScreen} />
            <Stack.Screen name="Orcamentos" component={OrcamentosScreen} />
            <Stack.Screen name="Metas" component={MetasScreen} />
          </>
        ) : (
          <Stack.Screen name="Auth" component={AuthStack} />
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
}
