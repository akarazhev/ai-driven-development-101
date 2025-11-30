import { useEffect, useState } from 'react'

const API = (path: string) => `${import.meta.env.VITE_API_BASE ?? 'http://localhost:8000'}/api${path}`

type Schedule = {
  id: number
  post_id: number
  status: string
  scheduled_at: string
  attempt_count: number
}

export default function Schedules() {
  const [rows, setRows] = useState<Schedule[]>([])
  const [busy, setBusy] = useState(false)

  const load = async () => {
    setBusy(true)
    try {
      const res = await fetch(API('/schedules'))
      const json = await res.json()
      setRows(json)
    } catch (e) {
      console.error(e)
      alert('Failed to load schedules')
    } finally {
      setBusy(false)
    }
  }

  useEffect(() => {
    load()
    const t = setInterval(load, 5000)
    return () => clearInterval(t)
  }, [])

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between">
        <h2 className="text-base font-semibold">Schedules</h2>
        <button onClick={load} disabled={busy} className="px-3 py-2 bg-gray-800 text-white rounded disabled:opacity-50">Refresh</button>
      </div>
      <div className="bg-white border rounded overflow-x-auto">
        <table className="min-w-full text-sm">
          <thead>
            <tr className="bg-gray-50 text-gray-700">
              <th className="text-left p-2 border-b">ID</th>
              <th className="text-left p-2 border-b">Post</th>
              <th className="text-left p-2 border-b">Status</th>
              <th className="text-left p-2 border-b">Scheduled</th>
              <th className="text-left p-2 border-b">Attempts</th>
            </tr>
          </thead>
          <tbody>
            {rows.map(r => (
              <tr key={r.id} className="odd:bg-white even:bg-gray-50">
                <td className="p-2 border-b">{r.id}</td>
                <td className="p-2 border-b">{r.post_id}</td>
                <td className="p-2 border-b">{r.status}</td>
                <td className="p-2 border-b">{new Date(r.scheduled_at).toLocaleString()}</td>
                <td className="p-2 border-b">{r.attempt_count}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
