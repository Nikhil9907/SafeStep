'use client';

import { useState } from 'react';

export default function Chatbot({ route }) {
  const [isOpen, setIsOpen] = useState(false);
  const [message, setMessage] = useState('');
  const [chat, setChat] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  const sendMessage = async () => {
    if (!message) return;

    const userMessage = { role: 'user', text: message };
    setChat(prev => [...prev, userMessage]);
    setMessage('');
    setIsLoading(true);

    try {
      const response = await fetch('http://localhost:8080/api/chat/explain', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          message,
          routeContext: route || null,
        }),
      });

      const data = await response.json();
      setChat(prev => [...prev, { role: 'bot', text: data.explanation }]);
    } catch (err) {
      setChat(prev => [...prev, { role: 'bot', text: 'Sorry, I could not connect to the server. Please try again!' }]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') sendMessage();
  };

  return (
    <div style={{
      position: 'absolute',
      bottom: '20px',
      right: '20px',
      zIndex: 1000,
    }}>
      {isOpen && (
        <div style={{
          background: 'white',
          borderRadius: '16px',
          padding: '1rem',
          width: '300px',
          boxShadow: '0 4px 20px rgba(0,0,0,0.15)',
          marginBottom: '10px',
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
            <h3 style={{ fontSize: '15px', fontWeight: 'bold', color: '#1a1a1a' }}>🤖 SafeStep AI</h3>
            <button onClick={() => setIsOpen(false)} style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '18px' }}>✕</button>
          </div>

          <div style={{
            height: '250px',
            overflowY: 'auto',
            marginBottom: '1rem',
            padding: '8px',
            background: '#f5f4f0',
            borderRadius: '10px',
          }}>
            {chat.length === 0 && (
              <p style={{ fontSize: '13px', color: '#888', textAlign: 'center', marginTop: '80px' }}>
                Ask me about route safety! 🛡️
              </p>
            )}
            {chat.map((msg, index) => (
              <div key={index} style={{
                display: 'flex',
                justifyContent: msg.role === 'user' ? 'flex-end' : 'flex-start',
                marginBottom: '8px',
              }}>
                <div style={{
                  background: msg.role === 'user' ? '#1a1a1a' : '#6c5ce7',
                  color: 'white',
                  padding: '8px 12px',
                  borderRadius: '10px',
                  fontSize: '13px',
                  maxWidth: '80%',
                }}>
                  {msg.text}
                </div>
              </div>
            ))}
            {isLoading && (
              <div style={{ display: 'flex', justifyContent: 'flex-start', marginBottom: '8px' }}>
                <div style={{ background: '#6c5ce7', color: 'white', padding: '8px 12px', borderRadius: '10px', fontSize: '13px' }}>
                  Thinking... 🤔
                </div>
              </div>
            )}
          </div>

          <div style={{ display: 'flex', gap: '8px' }}>
            <input
              type="text"
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Ask about safety..."
              style={{
                flex: 1,
                padding: '8px 12px',
                borderRadius: '8px',
                border: '1px solid #e0ddd5',
                fontSize: '13px',
                outline: 'none',
              }}
            />
            <button
              onClick={sendMessage}
              style={{
                padding: '8px 12px',
                background: '#6c5ce7',
                color: 'white',
                border: 'none',
                borderRadius: '8px',
                cursor: 'pointer',
                fontSize: '13px',
              }}
            >
              Send
            </button>
          </div>
        </div>
      )}

      <button
        onClick={() => setIsOpen(!isOpen)}
        style={{
          width: '56px',
          height: '56px',
          borderRadius: '50%',
          background: '#6c5ce7',
          color: 'white',
          border: 'none',
          fontSize: '24px',
          cursor: 'pointer',
          boxShadow: '0 4px 12px rgba(108,92,231,0.4)',
          display: 'block',
          marginLeft: 'auto',
        }}
      >
        🤖
      </button>
    </div>
  );
}