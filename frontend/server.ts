import express from 'express';
import { createServer as createViteServer } from 'vite';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

async function startServer() {
  const app = express();
  const PORT = 3000;

  app.use(express.json());

  // Mock API Routes
  app.post('/api/auth/login', (req, res) => {
    const { email, password } = req.body;
    // Mock authentication
    if (email === 'admin@securevote.com' && password === 'admin123') {
      return res.json({
        token: 'mock-admin-token',
        user: { id: 1, full_name: 'Admin User', email: 'admin@securevote.com', role: 'ADMIN' }
      });
    }
    if (email === 'voter@securevote.com' && password === 'voter123') {
      return res.json({
        token: 'mock-voter-token',
        user: { id: 2, full_name: 'Voter User', email: 'voter@securevote.com', role: 'VOTER' }
      });
    }
    res.status(401).json({ message: 'Invalid credentials' });
  });

  app.post('/api/auth/register', (req, res) => {
    res.json({ message: 'Registration successful! Please wait for admin approval.' });
  });

  app.get('/api/voter/profile', (req, res) => {
    res.json({ id: 2, full_name: 'Voter User', email: 'voter@securevote.com', role: 'VOTER', status: 'ACTIVE' });
  });

  app.get('/api/elections', (req, res) => {
    res.json([
      {
        id: 1,
        title: '2026 Presidential Election',
        description: 'National election for the next President.',
        start_time: '2026-04-01T00:00:00Z',
        end_time: '2026-04-30T23:59:59Z',
        is_active: true
      }
    ]);
  });

  app.get('/api/votes/status/:id', (req, res) => {
    res.json({ hasVoted: false });
  });

  app.get('/api/voter/notifications', (req, res) => {
    res.json([]);
  });

  // Admin Mock Routes
  app.get('/api/admin/voters', (req, res) => {
    res.json([
      { id: 2, full_name: 'Voter User', email: 'voter@securevote.com', role: 'VOTER', status: 'ACTIVE' }
    ]);
  });

  app.get('/api/admin/voters/pending', (req, res) => {
    res.json([]);
  });

  app.get('/api/admin/elections', (req, res) => {
    res.json([
      {
        id: 1,
        title: '2026 Presidential Election',
        description: 'National election for the next President.',
        start_time: '2026-04-01T00:00:00Z',
        end_time: '2026-04-30T23:59:59Z',
        is_active: true,
        categories: [
          { id: 1, category_name: 'President' }
        ]
      }
    ]);
  });

  app.get('/api/admin/candidates', (req, res) => {
    res.json([
      {
        id: 1,
        full_name: 'John Doe',
        party: 'Democratic Party',
        election_category_id: 1,
        category_name: 'President',
        election_title: '2026 Presidential Election'
      }
    ]);
  });

  app.get('/api/admin/audit-logs', (req, res) => {
    res.json([]);
  });

  app.get('/api/admin/announcements', (req, res) => {
    res.json([]);
  });

  app.get('/api/elections/:id', (req, res) => {
    res.json({
      id: req.params.id,
      title: '2026 Presidential Election',
      description: 'National election for the next President.',
      start_time: '2026-04-01T00:00:00Z',
      end_time: '2026-04-30T23:59:59Z',
      is_active: true,
      categories: [
        { id: 1, category_name: 'President' }
      ],
      candidates: [
        { id: 1, full_name: 'John Doe', party: 'Democratic Party', election_category_id: 1 }
      ]
    });
  });

  app.get('/api/admin/results/:id', (req, res) => {
    res.json([
      {
        category_id: 1,
        category_name: 'President',
        total_votes: 1250,
        turnout_percentage: 85,
        candidates: [
          { full_name: 'John Doe', party: 'Democratic Party', vote_count: 750 },
          { full_name: 'Jane Smith', party: 'Republican Party', vote_count: 500 }
        ]
      }
    ]);
  });

  app.get('/api/admin/results/:id/export/pdf', (req, res) => {
    res.send(Buffer.from('Mock PDF Content'));
  });

  app.get('/api/admin/results/:id/export/csv', (req, res) => {
    res.send('Candidate,Party,Votes\nJohn Doe,Democratic Party,750\nJane Smith,Republican Party,500');
  });

  app.get('/api/admin/voters/:id', (req, res) => {
    res.json({
      id: req.params.id,
      full_name: 'Voter User',
      email: 'voter@securevote.com',
      phone: '+1234567890',
      address: '123 Voting St, Election City',
      national_id: 'ID123456789',
      status: 'ACTIVE',
      email_verified: true,
      created_at: '2026-01-01T10:00:00Z'
    });
  });

  app.get('/api/admin/voters/:id/activity', (req, res) => {
    res.json([
      { action: 'LOGIN', details: 'User logged in', timestamp: new Date().toISOString() },
      { action: 'VOTE', details: 'User cast a vote in 2026 Presidential Election', timestamp: new Date().toISOString() }
    ]);
  });

  app.put('/api/admin/elections/:id/publish', (req, res) => {
    res.json({ message: 'Election published' });
  });

  app.put('/api/admin/elections/:id/lock-results', (req, res) => {
    res.json({ message: 'Results locked' });
  });

  app.delete('/api/admin/elections/:id', (req, res) => {
    res.json({ message: 'Election deleted' });
  });

  app.put('/api/admin/voters/:id/approve', (req, res) => {
    res.json({ message: 'Voter approved' });
  });

  app.put('/api/admin/voters/:id/deactivate', (req, res) => {
    res.json({ message: 'Voter deactivated' });
  });

  app.put('/api/admin/voters/:id/reset-password', (req, res) => {
    res.json({ message: 'Password reset' });
  });

  app.post('/api/admin/elections', (req, res) => {
    res.json({ id: Math.floor(Math.random() * 1000), ...req.body });
  });

  app.put('/api/admin/elections/:id', (req, res) => {
    res.json({ id: req.params.id, ...req.body });
  });

  app.post('/api/admin/candidates', (req, res) => {
    res.json({ id: Math.floor(Math.random() * 1000), ...req.body });
  });

  app.put('/api/admin/candidates/:id', (req, res) => {
    res.json({ id: req.params.id, ...req.body });
  });

  app.delete('/api/admin/candidates/:id', (req, res) => {
    res.json({ message: 'Candidate deleted' });
  });

  app.post('/api/admin/announcements', (req, res) => {
    res.json({ id: Math.floor(Math.random() * 1000), ...req.body });
  });

  app.put('/api/admin/announcements/:id', (req, res) => {
    res.json({ id: req.params.id, ...req.body });
  });

  app.delete('/api/admin/announcements/:id', (req, res) => {
    res.json({ message: 'Announcement deleted' });
  });

  app.put('/api/voter/notifications/:id/read', (req, res) => {
    res.json({ message: 'Notification marked as read' });
  });

  app.post('/api/votes/cast', (req, res) => {
    res.json({ message: 'Vote cast successfully' });
  });

  app.put('/api/voter/profile', (req, res) => {
    res.json({ message: 'Profile updated' });
  });

  app.put('/api/voter/profile/photo', (req, res) => {
    res.json({ message: 'Photo uploaded', photoUrl: 'https://picsum.photos/200' });
  });

  app.delete('/api/voter/account', (req, res) => {
    res.json({ message: 'Account deleted' });
  });

  app.get('/api/voter/history', (req, res) => {
    res.json([
      {
        id: 1,
        election_title: '2026 Presidential Election',
        voted_at: new Date().toISOString(),
        status: 'CONFIRMED'
      }
    ]);
  });

  app.get('/api/elections/active', (req, res) => {
    res.json([
      {
        id: 1,
        title: '2026 Presidential Election',
        description: 'National election for the next President.',
        start_time: '2026-04-01T00:00:00Z',
        end_time: '2026-04-30T23:59:59Z',
        is_active: true
      }
    ]);
  });

  app.get('/api/elections/upcoming', (req, res) => {
    res.json([]);
  });

  app.get('/api/elections/past', (req, res) => {
    res.json([]);
  });

  app.post('/api/auth/forgot-password', (req, res) => {
    res.json({ message: 'Password reset link sent to your email' });
  });

  app.post('/api/auth/reset-password', (req, res) => {
    res.json({ message: 'Password reset successful' });
  });

  app.get('/api/auth/verify-email', (req, res) => {
    res.json({ message: 'Email verified successfully' });
  });

  // Vite middleware for development
  if (process.env.NODE_ENV !== 'production') {
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: 'spa',
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.join(process.cwd(), 'dist');
    app.use(express.static(distPath));
    app.get('*', (req, res) => {
      res.sendFile(path.join(distPath, 'index.html'));
    });
  }

  app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server running on http://localhost:${PORT}`);
  });
}

startServer();
