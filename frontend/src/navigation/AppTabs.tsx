import React from "react";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { Ionicons } from "@expo/vector-icons";
import DashboardScreen from "../screens/app/DashboardScreen";
import TransacoesScreen from "../screens/app/TransacoesScreen";
import CategoriasScreen from "../screens/app/CategoriasScreen";
import PerfilScreen from "../screens/app/PerfilScreen";

export type AppTabsParamList = {
  Dashboard: undefined;
  Transacoes: undefined;
  Categorias: undefined;
  Perfil: undefined;
};

const Tab = createBottomTabNavigator<AppTabsParamList>();

export default function AppTabs() {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerShown: false,
        tabBarActiveTintColor: "#6366F1",
        tabBarInactiveTintColor: "#94A3B8",
        tabBarStyle: {
          backgroundColor: "#1E293B",
          borderTopColor: "#334155",
          borderTopWidth: 1,
          paddingBottom: 8,
          paddingTop: 8,
          height: 64,
        },
        tabBarLabelStyle: {
          fontFamily: "Inter_500Medium",
          fontSize: 11,
        },
        tabBarIcon: ({ focused, color, size }) => {
          let iconName: keyof typeof Ionicons.glyphMap;

          switch (route.name) {
            case "Dashboard":
              iconName = focused ? "grid" : "grid-outline";
              break;
            case "Transacoes":
              iconName = focused ? "swap-vertical" : "swap-vertical-outline";
              break;
            case "Categorias":
              iconName = focused ? "layers" : "layers-outline";
              break;
            case "Perfil":
              iconName = focused ? "person" : "person-outline";
              break;
            default:
              iconName = "ellipse-outline";
          }

          return <Ionicons name={iconName} size={size} color={color} />;
        },
      })}
    >
      <Tab.Screen
        name="Dashboard"
        component={DashboardScreen}
        options={{ tabBarLabel: "Início" }}
      />
      <Tab.Screen
        name="Transacoes"
        component={TransacoesScreen}
        options={{ tabBarLabel: "Transações" }}
      />
      <Tab.Screen
        name="Categorias"
        component={CategoriasScreen}
        options={{ tabBarLabel: "Categorias" }}
      />
      <Tab.Screen
        name="Perfil"
        component={PerfilScreen}
        options={{ tabBarLabel: "Perfil" }}
      />
    </Tab.Navigator>
  );
}
