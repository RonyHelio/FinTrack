import React, { createContext, useContext, useState, useCallback, useRef } from "react";
import { View, Text, Animated } from "react-native";
import { Ionicons } from "@expo/vector-icons";

export type NotificationType = "success" | "error" | "info" | "warning";

interface NotificationContextData {
  showNotification: (message: string, type?: NotificationType) => void;
}

const NotificationContext = createContext<NotificationContextData>({} as NotificationContextData);

export function NotificationProvider({ children }: { children: React.ReactNode }) {
  const [visible, setVisible] = useState(false);
  const [message, setMessage] = useState("");
  const [type, setType] = useState<NotificationType>("success");
  const opacity = useRef(new Animated.Value(0)).current;

  const showNotification = useCallback((msg: string, notifType: NotificationType = "success") => {
    setMessage(msg);
    setType(notifType);
    setVisible(true);

    Animated.timing(opacity, {
      toValue: 1,
      duration: 300,
      useNativeDriver: true,
    }).start();

    setTimeout(() => {
      Animated.timing(opacity, {
        toValue: 0,
        duration: 300,
        useNativeDriver: true,
      }).start(() => setVisible(false));
    }, 3000);
  }, [opacity]);

  const getBackgroundColor = () => {
    switch (type) {
      case "success":
        return "bg-success-500";
      case "error":
        return "bg-danger-500";
      case "warning":
        return "bg-warning-500";
      default:
        return "bg-primary-500";
    }
  };

  const getIconName = () => {
    switch (type) {
      case "success":
        return "checkmark-circle";
      case "error":
        return "alert-circle";
      case "warning":
        return "warning";
      default:
        return "information-circle";
    }
  };

  return (
    <NotificationContext.Provider value={{ showNotification }}>
      {children}
      {visible && (
        <Animated.View
          style={{ opacity }}
          className={`absolute bottom-24 left-4 right-4 p-4 rounded-xl flex-row items-center shadow-lg ${getBackgroundColor()}`}
          pointerEvents="none"
        >
          <Ionicons name={getIconName()} size={24} color="#FFFFFF" />
          <Text className="text-white ml-3 font-semibold flex-1" style={{ fontFamily: "Inter_600SemiBold" }}>
            {message}
          </Text>
        </Animated.View>
      )}
    </NotificationContext.Provider>
  );
}

export function useNotification() {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error("useNotification must be used within a NotificationProvider");
  }
  return context;
}
