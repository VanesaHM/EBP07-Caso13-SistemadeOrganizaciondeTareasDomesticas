import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import {
  clearSession,
  getActiveSession,
  getTokenExpiryTime,
  isTokenExpiringSoon,
  TOKEN_CHECK_INTERVAL,
  touchSession,
} from "./lib/session";
import { Alert, AlertDescription } from "./components/ui/alert";
import { AlertCircle } from "lucide-react";

export function SessionManager({ children }: { children: React.ReactNode }) {
  const navigate = useNavigate();
  const [showWarning, setShowWarning] = useState(false);
  const [timeRemaining, setTimeRemaining] = useState(0);

  useEffect(() => {
    const handleUserActivity = () => {
      touchSession();
    };

    document.addEventListener("mousedown", handleUserActivity);
    document.addEventListener("keydown", handleUserActivity);
    document.addEventListener("scroll", handleUserActivity, true);
    document.addEventListener("touchstart", handleUserActivity);

    const checkInterval = window.setInterval(() => {
      const session = getActiveSession();

      if (!session) {
        setShowWarning(false);
        return;
      }

      if (isTokenExpiringSoon()) {
        setShowWarning(true);
        const expiryTime = getTokenExpiryTime();
        if (expiryTime) {
          const remaining = Math.max(0, expiryTime - Date.now());
          setTimeRemaining(Math.ceil(remaining / 1000));
        }
      } else {
        setShowWarning(false);
      }
    }, TOKEN_CHECK_INTERVAL);

    return () => {
      document.removeEventListener("mousedown", handleUserActivity);
      document.removeEventListener("keydown", handleUserActivity);
      document.removeEventListener("scroll", handleUserActivity, true);
      document.removeEventListener("touchstart", handleUserActivity);
      window.clearInterval(checkInterval);
    };
  }, []);

  useEffect(() => {
    if (!showWarning || timeRemaining <= 0) {
      return;
    }

    const timer = window.setInterval(() => {
      setTimeRemaining((prev) => {
        const newTime = prev - 1;
        if (newTime <= 0) {
          clearSession();
          navigate("/");
          return 0;
        }
        return newTime;
      });
    }, 1000);

    return () => window.clearInterval(timer);
  }, [showWarning, timeRemaining, navigate]);

  return (
    <>
      {showWarning && (
        <div className="fixed top-4 left-4 right-4 z-50 max-w-md">
          <Alert variant="destructive">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>
              Tu sesion esta proxima a expirar en {timeRemaining} segundos. Se cerrara automaticamente.
            </AlertDescription>
          </Alert>
        </div>
      )}
      {children}
    </>
  );
}
