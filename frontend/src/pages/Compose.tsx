import { useEffect, useMemo, useRef, useState } from 'react'

const API = (path: string) => `${import.meta.env.VITE_API_BASE ?? 'http://localhost:8000'}/api${path}`

type Media = { id: number; filename: string; alt_text?: string }

export default function Compose() {
  const [text, setText] = useState('')
  const [files, setFiles] = useState<FileList | null>(null)
  const [media, setMedia] = useState<Media[]>([])
  const [busy, setBusy] = useState(false)
  const [variants, setVariants] = useState<string[]>([])
  const [postId, setPostId] = useState<number | null>(null)
  const [scheduleId, setScheduleId] = useState<number | null>(null)
  const altInputs = useRef<HTMLInputElement[]>([])

  const canUpload = useMemo(() => files && files.length > 0, [files])

  const onFiles = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFiles(e.target.files)
  }

  const uploadAll = async () => {
    if (!files) return
    setBusy(true)
    try {
      const uploaded: Media[] = []
      for (let i = 0; i < files.length; i++) {
        const f = files[i]
        const fd = new FormData()
        fd.append('file', f)
        const alt = altInputs.current[i]?.value
        if (alt) fd.append('alt_text', alt)
        const res = await fetch(API('/media'), { method: 'POST', body: fd })
        if (!res.ok) throw new Error('upload_failed')
        const json = await res.json()
        uploaded.push(json)
      }
      setMedia(prev => [...prev, ...uploaded])
      setFiles(null)
    } catch (e) {
      console.error(e)
      alert('Upload failed')
    } finally {
      setBusy(false)
    }
  }

  const makeVariants = async () => {
    setBusy(true)
    try {
      const res = await fetch(API('/ai/variants'), {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: text })
      })
      const json = await res.json()
      setVariants(json.variants ?? [])
    } catch (e) {
      console.error(e)
      alert('Variant generation failed')
    } finally {
      setBusy(false)
    }
  }

  const createPost = async () => {
    setBusy(true)
    try {
      const res = await fetch(API('/posts'), {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ text, media_ids: media.map(m => m.id) })
      })
      if (!res.ok) throw new Error('post_failed')
      const json = await res.json()
      setPostId(json.id)
      alert('Post created')
    } catch (e) {
      console.error(e)
      alert('Post creation failed')
    } finally {
      setBusy(false)
    }
  }

  const publishNow = async () => {
    if (!postId) return alert('Create a post first')
    setBusy(true)
    try {
      const res = await fetch(API('/providers/default/publish'), {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ post_id: postId })
      })
      const json = await res.json()
      alert(`Published with status: ${json.status}`)
    } catch (e) {
      console.error(e)
      alert('Publish failed')
    } finally {
      setBusy(false)
    }
  }

  const schedule = async () => {
    if (!postId) return alert('Create a post first')
    setBusy(true)
    try {
      const res = await fetch(API('/schedules'), {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ post_id: postId })
      })
      const json = await res.json()
      setScheduleId(json.id)
      alert('Scheduled')
    } catch (e) {
      console.error(e)
      alert('Schedule failed')
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="space-y-8">
      <section className="space-y-3">
        <h2 className="text-base font-semibold">Compose</h2>
        <textarea
          className="w-full border rounded p-3 focus:outline-none focus:ring"
          rows={4}
          placeholder="Write your post..."
          value={text}
          onChange={e => setText(e.target.value)}
        />
        <div className="flex gap-2">
          <button onClick={makeVariants} disabled={busy || !text} className="px-3 py-2 bg-blue-600 text-white rounded disabled:opacity-50">Generate variants</button>
          <button onClick={createPost} disabled={busy || !text} className="px-3 py-2 bg-green-600 text-white rounded disabled:opacity-50">Create post</button>
          <button onClick={publishNow} disabled={busy || !postId} className="px-3 py-2 bg-purple-600 text-white rounded disabled:opacity-50">Publish now</button>
          <button onClick={schedule} disabled={busy || !postId} className="px-3 py-2 bg-amber-600 text-white rounded disabled:opacity-50">Schedule</button>
        </div>
        {variants.length > 0 && (
          <div className="bg-white border rounded p-3">
            <h3 className="font-medium mb-2">Variants</h3>
            <ul className="list-disc pl-5 space-y-1">
              {variants.map((v, i) => (
                <li key={i}>
                  <button className="text-blue-600 hover:underline" onClick={() => setText(v)}>{v}</button>
                </li>
              ))}
            </ul>
          </div>
        )}
      </section>

      <section className="space-y-3">
        <h2 className="text-base font-semibold">Media</h2>
        <input type="file" multiple accept="image/*" onChange={onFiles} />
        {files && files.length > 0 && (
          <div className="space-y-2">
            {Array.from(files).map((f, idx) => (
              <div key={idx} className="flex items-center gap-2 text-sm">
                <div className="text-gray-600">{f.name}</div>
                <input ref={el => { if (el) altInputs.current[idx] = el }} className="border rounded p-1 flex-1" placeholder="Alt text (optional)" />
              </div>
            ))}
            <button onClick={uploadAll} disabled={busy || !canUpload} className="px-3 py-2 bg-gray-800 text-white rounded disabled:opacity-50">Upload</button>
          </div>
        )}
        {media.length > 0 && (
          <div className="bg-white border rounded p-3">
            <h3 className="font-medium mb-2">Attached</h3>
            <ul className="list-disc pl-5 text-sm space-y-1">
              {media.map(m => (
                <li key={m.id}>{m.filename} {m.alt_text ? `(alt: ${m.alt_text})` : ''}</li>
              ))}
            </ul>
          </div>
        )}
      </section>
    </div>
  )
}
