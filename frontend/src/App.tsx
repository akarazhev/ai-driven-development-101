import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom'
import Compose from './pages/Compose'
import Schedules from './pages/Schedules'

export default function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen flex flex-col">
        <header className="bg-white border-b">
          <div className="max-w-5xl mx-auto px-4 py-3 flex items-center justify-between">
            <h1 className="text-lg font-semibold">Social Media Automation</h1>
            <nav className="flex gap-4 text-sm">
              <NavLink to="/" end className={({isActive}) => isActive ? 'text-blue-600 font-medium' : 'text-gray-600 hover:text-blue-600'}>Compose</NavLink>
              <NavLink to="/schedules" className={({isActive}) => isActive ? 'text-blue-600 font-medium' : 'text-gray-600 hover:text-blue-600'}>Schedules</NavLink>
            </nav>
          </div>
        </header>
        <main className="flex-1 max-w-5xl mx-auto px-4 py-6">
          <Routes>
            <Route path="/" element={<Compose />} />
            <Route path="/schedules" element={<Schedules />} />
          </Routes>
        </main>
        <footer className="border-t bg-white text-xs text-gray-500">
          <div className="max-w-5xl mx-auto px-4 py-3">© {new Date().getFullYear()} Social App</div>
        </footer>
      </div>
    </BrowserRouter>
  )
}
