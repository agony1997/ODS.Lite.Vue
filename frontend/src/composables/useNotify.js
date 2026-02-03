import { useQuasar } from 'quasar'

export function useNotify() {
  const $q = useQuasar()

  function notifySuccess(message) {
    $q.notify({ type: 'positive', message })
  }

  function notifyError(message) {
    $q.notify({ type: 'negative', message })
  }

  function notifyWarning(message) {
    $q.notify({ type: 'warning', message })
  }

  function handleError(err) {
    const message = err?.message || '發生未預期的錯誤'
    console.error(message, err)
    notifyError(message)
  }

  function confirmAction(message) {
    return new Promise((resolve) => {
      $q.dialog({
        title: '確認',
        message,
        cancel: { label: '取消', flat: true },
        ok: { label: '確定', color: 'primary' },
        persistent: true
      })
        .onOk(() => resolve(true))
        .onCancel(() => resolve(false))
        .onDismiss(() => resolve(false))
    })
  }

  return {
    notifySuccess,
    notifyError,
    notifyWarning,
    handleError,
    confirmAction
  }
}
