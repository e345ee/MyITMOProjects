import { ReactNode, useEffect, useRef } from 'react';
import { createPortal } from 'react-dom';
import { X } from 'lucide-react';

type Props = {
  title: string;
  subtitle?: string;
  children: ReactNode;
  onClose: () => void;
  /** 360–560px is a good sweet spot for forms */
  maxWidthPx?: number;
};

/**
 * A small, dependency-free modal that always sits on top of the UI.
 *
 * NOTE: This project does not use Tailwind in build deps, so we rely on inline styles
 * for critical layout (centering, width, z-index). You can still pass className-like
 * strings in children, but modal positioning must be rock-solid.
 */
export function PortalModal({ title, subtitle, children, onClose, maxWidthPx = 520 }: Props) {
  const panelRef = useRef<HTMLDivElement | null>(null);
  // Keep the latest onClose without re-running the mount effect.
  const onCloseRef = useRef(onClose);

  useEffect(() => {
    onCloseRef.current = onClose;
  }, [onClose]);

  useEffect(() => {
    // Focus the modal panel once on mount so keyboard users can close it with Escape.
    // IMPORTANT: do NOT re-focus on every render (otherwise inputs lose focus while typing).
    panelRef.current?.focus();

    const onKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onCloseRef.current();
    };
    window.addEventListener('keydown', onKeyDown);
    return () => window.removeEventListener('keydown', onKeyDown);
  }, []);

  return createPortal(
    <div
      role="dialog"
      aria-modal="true"
      onMouseDown={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
      style={{
        position: 'fixed',
        inset: 0,
        zIndex: 9999,
        background: 'rgba(0,0,0,0.40)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: 16,
      }}
      className="df-modal-backdrop"
    >
      <div
        onMouseDown={(e) => e.stopPropagation()}
        ref={panelRef}
        tabIndex={-1}
        style={{
          width: '100%',
          maxWidth: maxWidthPx,
          background: '#fff',
          border: '1px solid #e5e7eb',
          borderRadius: 16,
          padding: 24,
          boxShadow: '0 20px 60px rgba(0,0,0,0.18)',
        }}
        className="df-modal-panel"
      >
        <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: 12 }}>
          <div>
            <div style={{ fontSize: 20, fontWeight: 700, color: '#111827', lineHeight: 1.2 }}>{title}</div>
            {subtitle && (
              <div style={{ marginTop: 6, fontSize: 14, color: '#6b7280', lineHeight: 1.4 }}>{subtitle}</div>
            )}
          </div>
          <button
            onClick={onClose}
            aria-label="Закрыть"
            style={{
              border: 'none',
              background: 'transparent',
              cursor: 'pointer',
              padding: 8,
              borderRadius: 10,
              color: '#6b7280',
            }}
          >
            <X size={18} />
          </button>
        </div>

        <div style={{ marginTop: 16 }}>{children}</div>
      </div>
    </div>,
    document.body
  );
}
